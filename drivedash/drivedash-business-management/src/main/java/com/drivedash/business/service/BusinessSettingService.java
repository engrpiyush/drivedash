package com.drivedash.business.service;

import com.drivedash.business.entity.BusinessSetting;
import com.drivedash.business.entity.SettingsType;
import com.drivedash.business.repository.BusinessSettingRepository;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.core.util.FileStorageService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Core settings service – mirrors Laravel's {@code BusinessSettingService}.
 *
 * <p>All upsert operations follow the same pattern as the PHP original:
 * look up by ({@code keyName}, {@code settingsType}); update if found, create otherwise.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessSettingService {

    private final BusinessSettingRepository repository;
    private final FileStorageService fileStorageService;

    // ── Generic upsert ──────────────────────────────────────────────────────

    /**
     * Upserts a single key-value pair for the given settings type.
     * The value is wrapped in a single-entry map if it is a scalar;
     * callers that need structured values should pass a {@code Map} directly
     * via {@link #upsertMap(String, SettingsType, Map)}.
     */
    @Transactional
    public BusinessSetting upsert(String keyName, SettingsType settingsType, Object scalarValue) {
        return upsertMap(keyName, settingsType, Map.of("v", scalarValue));
    }

    /**
     * Upserts a structured map value for the given settings type.
     * This is the primary write path used by all sub-services.
     */
    @Transactional
    public BusinessSetting upsertMap(String keyName, SettingsType settingsType,
                                      Map<String, Object> value) {
        Optional<BusinessSetting> existing =
                repository.findByKeyNameAndSettingsType(keyName, settingsType);

        if (existing.isPresent()) {
            BusinessSetting setting = existing.get();
            setting.setValue(value);
            return repository.save(setting);
        }

        return repository.save(BusinessSetting.builder()
                .keyName(keyName)
                .settingsType(settingsType)
                .value(value)
                .build());
    }

    // ── Reads ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<BusinessSetting> find(String keyName, SettingsType settingsType) {
        return repository.findByKeyNameAndSettingsType(keyName, settingsType);
    }

    @Transactional(readOnly = true)
    public List<BusinessSetting> findAllByType(SettingsType settingsType) {
        return repository.findAllBySettingsType(settingsType);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getValue(String keyName, SettingsType settingsType) {
        return find(keyName, settingsType)
                .map(BusinessSetting::getValue)
                .orElse(Map.of());
    }

    /** Convenience: get a scalar setting stored as {@code {"v": value}}. */
    @Transactional(readOnly = true)
    public Object getScalar(String keyName, SettingsType settingsType) {
        return getValue(keyName, settingsType).get("v");
    }

    // ── Business Information ────────────────────────────────────────────────

    @Transactional
    public void storeBusinessInfo(Map<String, Object> data, Map<String, MultipartFile> files) {
        String[] imageKeys = {"header_logo", "favicon", "preloader"};

        for (String imageKey : imageKeys) {
            if (files.containsKey(imageKey) && !files.get(imageKey).isEmpty()) {
                String existingPath = (String) find(imageKey, SettingsType.BUSINESS_INFORMATION)
                        .map(bs -> bs.getValue().get("v")).orElse(null);
                if (existingPath != null) {
                    fileStorageService.delete(existingPath);
                }
                String storedPath = fileStorageService.store(files.get(imageKey), "business");
                data.put(imageKey, storedPath);
            }
        }

        // Resolve currency symbol from code
        String currencyCode = (String) data.getOrDefault("currency_code", "USD");
        data.put("currency_code", currencyCode);

        // Boolean toggles – absent from form means 0
        String[] booleanKeys = {
            "driver_verification", "customer_verification", "email_verification",
            "driver_self_registration", "otp_verification"
        };
        for (String key : booleanKeys) {
            data.putIfAbsent(key, 0);
        }

        data.forEach((key, value) -> upsert(key, SettingsType.BUSINESS_INFORMATION, value));
    }

    // ── Business Settings (general) ─────────────────────────────────────────

    @Transactional
    public void updateBusinessSettings(Map<String, Object> data) {
        if (data.containsKey("bid_on_fare")) {
            data.put("bid_on_fare", 1);
        } else {
            data.put("bid_on_fare", 0);
        }
        data.forEach((key, value) -> {
            if (value != null) {
                upsert(key, SettingsType.BUSINESS_SETTINGS, value);
            }
        });
    }

    // ── Maintenance mode ────────────────────────────────────────────────────

    @Transactional
    public void setMaintenanceMode(boolean enabled) {
        upsert("maintenance_mode", SettingsType.BUSINESS_INFORMATION, enabled ? 1 : 0);
    }

    // ── Driver / Customer settings ──────────────────────────────────────────

    @Transactional
    public void storeDriverSettings(Map<String, Object> data) {
        data.forEach((key, value) -> upsert(key, SettingsType.DRIVER_SETTINGS, value));
    }

    @Transactional
    public void storeCustomerSettings(Map<String, Object> data) {
        data.forEach((key, value) -> upsert(key, SettingsType.CUSTOMER_SETTINGS, value));
    }

    // ── Trip / fare settings ────────────────────────────────────────────────

    @Transactional
    public void storeTripFareSettings(Map<String, Object> data) {
        data.putIfAbsent("bidding_push_notification", 0);
        data.putIfAbsent("trip_push_notification", 0);
        data.forEach((key, value) -> upsert(key, SettingsType.TRIP_SETTINGS, value));
    }

    // ── Business pages ──────────────────────────────────────────────────────

    @Transactional
    public void storeBusinessPage(String pageType, String shortDescription,
                                   String longDescription, MultipartFile image) {
        Map<String, Object> existing = getValue(pageType, SettingsType.PAGES_SETTINGS);
        String imagePath = (String) existing.getOrDefault("image", "");

        if (image != null && !image.isEmpty()) {
            if (!imagePath.isBlank()) {
                fileStorageService.delete(imagePath);
            }
            imagePath = fileStorageService.store(image, "business/pages");
        }

        upsertMap(pageType, SettingsType.PAGES_SETTINGS, Map.of(
                "name", pageType,
                "short_description", shortDescription,
                "long_description", longDescription,
                "image", imagePath
        ));
    }

    // ── Email config ────────────────────────────────────────────────────────

    @Transactional
    public void storeEmailConfig(Map<String, Object> data) {
        upsertMap("email_config", SettingsType.EMAIL_CONFIG, data);
    }

    // ── Google Map API ──────────────────────────────────────────────────────

    @Transactional
    public void storeGoogleMapApi(Map<String, Object> data) {
        upsertMap("google_map_api", SettingsType.GOOGLE_MAP_API, data);
    }

    // ── Recaptcha ───────────────────────────────────────────────────────────

    @Transactional
    public void storeRecaptcha(Map<String, Object> data) {
        upsertMap("recaptcha", SettingsType.RECAPTCHA, data);
    }

    // ── SMS config ──────────────────────────────────────────────────────────

    @Transactional
    public void storeSmsConfig(Map<String, Object> data) {
        upsertMap("sms_config", SettingsType.SMS_CONFIG, data);
    }

    // ── Payment config ──────────────────────────────────────────────────────

    @Transactional
    public void storePaymentConfig(Map<String, Object> data) {
        upsertMap("payment_config", SettingsType.PAYMENT_CONFIG, data);
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        BusinessSetting setting = repository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Setting not found"));
        repository.delete(setting);
    }
}
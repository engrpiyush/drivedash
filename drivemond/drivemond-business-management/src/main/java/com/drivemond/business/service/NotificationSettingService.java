package com.drivemond.business.service;

import com.drivemond.business.entity.NotificationSetting;
import com.drivemond.business.repository.NotificationSettingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages per-event push/email notification toggles.
 * Mirrors Laravel's {@code NotificationSettingService}.
 */
@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository repository;

    @Transactional(readOnly = true)
    public List<NotificationSetting> findAll() {
        return repository.findAll();
    }

    /**
     * Upserts a notification setting by name.
     * If no record exists for the given name, a new one is created.
     */
    @Transactional
    public NotificationSetting upsert(String name, boolean push, boolean email) {
        NotificationSetting setting = repository.findByName(name)
                .orElseGet(() -> NotificationSetting.builder().name(name).build());
        setting.setPush(push);
        setting.setEmail(email);
        return repository.save(setting);
    }

    /**
     * Batch upsert from a map of {@code name -> {push, email}} pairs.
     * Called when the admin saves the entire notification settings page.
     */
    @Transactional
    public void batchUpsert(List<NotificationSetting> settings) {
        for (NotificationSetting incoming : settings) {
            upsert(incoming.getName(), incoming.isPush(), incoming.isEmail());
        }
    }
}

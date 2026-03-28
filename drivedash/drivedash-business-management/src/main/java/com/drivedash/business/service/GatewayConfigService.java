package com.drivedash.business.service;

import com.drivedash.business.dto.GatewayInfo;
import com.drivedash.business.entity.BusinessSetting;
import com.drivedash.business.entity.SettingsType;
import com.drivedash.core.exception.DrivedashException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages payment gateway configurations stored in {@code business_settings}
 * (settings_type = PAYMENT_CONFIG, key_name = &lt;gateway-slug&gt;).
 *
 * <p>Mirrors {@code Modules/Payment/Http/Controllers/GatewayController} from the PHP source.
 * Supports 31 gateways; credentials are persisted as JSON with separate
 * {@code live_values} and {@code test_values} sub-objects.
 */
@Service
@RequiredArgsConstructor
public class GatewayConfigService {

    private final BusinessSettingService settingService;

    // ── Static catalogue ─────────────────────────────────────────────────────

    private record GatewayDef(String slug, String title, String icon, List<String> fields) {}

    private static final List<GatewayDef> CATALOGUE = List.of(
        new GatewayDef("stripe",        "Stripe",        "bi-stripe",                 List.of("secret_key", "publishable_key", "webhook_secret")),
        new GatewayDef("paypal",        "PayPal",        "bi-paypal",                 List.of("client_id", "client_secret", "app_id")),
        new GatewayDef("razorpay",      "Razorpay",      "bi-credit-card-2-back",     List.of("key_id", "key_secret", "webhook_secret")),
        new GatewayDef("paystack",      "Paystack",      "bi-cash-stack",             List.of("public_key", "secret_key")),
        new GatewayDef("flutterwave",   "Flutterwave",   "bi-currency-exchange",      List.of("public_key", "secret_key", "hash")),
        new GatewayDef("sslcommerz",    "SSLCommerz",    "bi-shield-check",           List.of("store_id", "store_passwd")),
        new GatewayDef("paymob",        "Paymob",        "bi-credit-card",            List.of("api_key", "integration_id", "hmac_secret")),
        new GatewayDef("bkash",         "bKash",         "bi-phone",                  List.of("app_key", "app_secret", "username", "password")),
        new GatewayDef("nagad",         "Nagad",         "bi-phone-fill",             List.of("merchant_id", "merchant_number", "public_key", "private_key")),
        new GatewayDef("midtrans",      "Midtrans",      "bi-wallet2",                List.of("server_key", "client_key")),
        new GatewayDef("mercadopago",   "MercadoPago",   "bi-currency-dollar",        List.of("access_token", "public_key")),
        new GatewayDef("liqpay",        "LiqPay",        "bi-bank",                   List.of("public_key", "private_key")),
        new GatewayDef("paytm",         "Paytm",         "bi-phone",                  List.of("merchant_id", "merchant_key", "website", "channel", "industry_type")),
        new GatewayDef("myfatoorah",    "MyFatoorah",    "bi-globe",                  List.of("api_key")),
        new GatewayDef("khalti",        "Khalti",        "bi-wallet",                 List.of("public_key", "secret_key")),
        new GatewayDef("esewa",         "eSewa",         "bi-wallet",                 List.of("merchant_code")),
        new GatewayDef("iyzico",        "iyzico",        "bi-credit-card",            List.of("api_key", "secret_key")),
        new GatewayDef("pagseguro",     "PagSeguro",     "bi-currency-dollar",        List.of("email", "token")),
        new GatewayDef("authorize_net", "Authorize.Net", "bi-bank",                   List.of("login_id", "transaction_key", "signature_key")),
        new GatewayDef("square",        "Square",        "bi-square",                 List.of("application_id", "location_id", "access_token")),
        new GatewayDef("braintree",     "Braintree",     "bi-diagram-3",              List.of("merchant_id", "public_key", "private_key")),
        new GatewayDef("xendit",        "Xendit",        "bi-credit-card",            List.of("secret_key", "public_key")),
        new GatewayDef("instamojo",     "Instamojo",     "bi-lightning",              List.of("api_key", "auth_token")),
        new GatewayDef("cashfree",      "Cashfree",      "bi-cash",                   List.of("app_id", "secret_key")),
        new GatewayDef("senang_pay",    "SenangPay",     "bi-credit-card",            List.of("secret_key", "merchant_id")),
        new GatewayDef("foloosi",       "Foloosi",       "bi-globe",                  List.of("secret_key")),
        new GatewayDef("tap",           "Tap",           "bi-cursor",                 List.of("secret_key", "public_key")),
        new GatewayDef("telr",          "Telr",          "bi-bank2",                  List.of("store_id", "auth_key")),
        new GatewayDef("billplz",       "Billplz",       "bi-receipt",                List.of("api_key", "collection_id")),
        new GatewayDef("amarpay",       "AmarPay",       "bi-wallet",                 List.of("store_id", "integration_key", "signature_key")),
        new GatewayDef("ngenius",       "N-Genius",      "bi-credit-card-2-front",    List.of("api_key", "outlet_id"))
    );

    // ── Reads ────────────────────────────────────────────────────────────────

    /**
     * Returns all 31 gateways merged with whatever is stored in business_settings.
     * Un-configured gateways appear with empty credential maps and status = 0.
     */
    @Transactional(readOnly = true)
    public List<GatewayInfo> getAllGateways() {
        Map<String, Map<String, Object>> stored = loadStoredConfigs();
        return CATALOGUE.stream()
                .map(def -> toGatewayInfo(def, stored.getOrDefault(def.slug(), Map.of())))
                .collect(Collectors.toList());
    }

    /**
     * Returns a single gateway merged with its stored config.
     *
     * @throws DrivedashException if {@code slug} is not in the catalogue
     */
    @Transactional(readOnly = true)
    public GatewayInfo getGateway(String slug) {
        GatewayDef def = findDef(slug);
        Map<String, Object> stored = settingService
                .getValue(slug, SettingsType.PAYMENT_CONFIG);
        return toGatewayInfo(def, stored);
    }

    /**
     * Returns active gateways with public-safe config only (no secret keys).
     * Used by the mobile-app API so secrets are never sent to devices.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveGatewaysPublic() {
        Map<String, Map<String, Object>> stored = loadStoredConfigs();
        List<Map<String, Object>> result = new ArrayList<>();

        for (GatewayDef def : CATALOGUE) {
            Map<String, Object> cfg = stored.getOrDefault(def.slug(), Map.of());
            if (!Integer.valueOf(1).equals(cfg.get("status"))) continue;

            String mode = (String) cfg.getOrDefault("mode", "test");
            @SuppressWarnings("unchecked")
            Map<String, Object> modeValues = (Map<String, Object>) cfg.getOrDefault(
                    mode + "_values", Map.of());

            // Strip secret fields — only keep publishable / public keys
            Map<String, Object> publicValues = new LinkedHashMap<>();
            modeValues.forEach((k, v) -> {
                if (k.contains("publish") || k.contains("public") || k.equals("app_id")
                        || k.equals("client_id") || k.equals("merchant_id")
                        || k.equals("merchant_code") || k.equals("integration_id")
                        || k.equals("location_id") || k.equals("outlet_id")
                        || k.equals("collection_id") || k.equals("store_id")) {
                    publicValues.put(k, v);
                }
            });

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("slug", def.slug());
            entry.put("title", def.title());
            entry.put("mode", mode);
            entry.put("credentials", publicValues);
            result.add(entry);
        }

        return result;
    }

    // ── Writes ───────────────────────────────────────────────────────────────

    /**
     * Persists gateway config parsed from the admin form.
     * Form fields are prefixed: {@code live_<field>} and {@code test_<field>}.
     */
    @Transactional
    public void updateFromParams(String slug, Map<String, String> params) {
        GatewayDef def = findDef(slug);
        int status = "1".equals(params.get("status")) ? 1 : 0;
        String mode = params.getOrDefault("mode", "test");

        Map<String, Object> liveValues = new LinkedHashMap<>();
        Map<String, Object> testValues = new LinkedHashMap<>();
        for (String field : def.fields()) {
            liveValues.put(field, params.getOrDefault("live_" + field, ""));
            testValues.put(field, params.getOrDefault("test_" + field, ""));
        }

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("gateway_title", def.title());
        config.put("status", status);
        config.put("mode", mode);
        config.put("live_values", liveValues);
        config.put("test_values", testValues);

        settingService.upsertMap(slug, SettingsType.PAYMENT_CONFIG, config);
    }

    /**
     * Toggles the {@code status} field (0 ↔ 1) without touching credentials.
     */
    @Transactional
    public void toggleStatus(String slug) {
        GatewayDef def = findDef(slug);
        Map<String, Object> existing = new LinkedHashMap<>(
                settingService.getValue(slug, SettingsType.PAYMENT_CONFIG));

        int current = toInt(existing.getOrDefault("status", 0));
        existing.put("status", current == 1 ? 0 : 1);
        if (!existing.containsKey("gateway_title")) {
            existing.put("gateway_title", def.title());
        }

        settingService.upsertMap(slug, SettingsType.PAYMENT_CONFIG, existing);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Map<String, Object>> loadStoredConfigs() {
        return settingService.findAllByType(SettingsType.PAYMENT_CONFIG)
                .stream()
                .collect(Collectors.toMap(
                        BusinessSetting::getKeyName,
                        BusinessSetting::getValue));
    }

    private GatewayDef findDef(String slug) {
        return CATALOGUE.stream()
                .filter(d -> d.slug().equals(slug))
                .findFirst()
                .orElseThrow(() -> DrivedashException.notFound("Payment gateway not found: " + slug));
    }

    @SuppressWarnings("unchecked")
    private GatewayInfo toGatewayInfo(GatewayDef def, Map<String, Object> stored) {
        int status = toInt(stored.getOrDefault("status", 0));
        String mode = (String) stored.getOrDefault("mode", "test");
        Map<String, Object> live = (Map<String, Object>) stored.getOrDefault("live_values", Map.of());
        Map<String, Object> test = (Map<String, Object>) stored.getOrDefault("test_values", Map.of());

        return GatewayInfo.builder()
                .slug(def.slug())
                .title(def.title())
                .iconClass(def.icon())
                .credentialFields(def.fields())
                .status(status)
                .mode(mode)
                .liveValues(new LinkedHashMap<>(live))
                .testValues(new LinkedHashMap<>(test))
                .build();
    }

    private int toInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }
}

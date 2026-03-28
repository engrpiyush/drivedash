package com.drivedash.business.controller.web;

import com.drivedash.business.dto.GatewayInfo;
import com.drivedash.business.service.GatewayConfigService;
import com.drivedash.business.service.PaymentRequestService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin UI for managing the 31 supported payment gateways.
 *
 * <p>Routes: /admin/business/configuration/payment-gateways/**
 *
 * <p>Gateway credentials are stored via {@link GatewayConfigService} in
 * {@code business_settings} (settings_type = PAYMENT_CONFIG). No PHP migration
 * equivalent — this consolidates the PHP {@code GatewayController} and
 * gateway-specific sub-controllers into a single unified admin.
 */
@Controller
@RequestMapping("/admin/business/configuration/payment-gateways")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class PaymentGatewayWebController {

    private final GatewayConfigService gatewayConfigService;
    private final PaymentRequestService paymentRequestService;

    // ── List ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String index(Model model) {
        List<GatewayInfo> gateways = gatewayConfigService.getAllGateways();
        long activeCount  = gateways.stream().filter(GatewayInfo::isActive).count();
        long pendingCount = paymentRequestService.getPage(0, 1).getTotalElements();

        model.addAttribute("gateways", gateways);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalCount", gateways.size());
        model.addAttribute("pendingPayments", pendingCount);
        return "admin/business/configuration/payment-gateways";
    }

    // ── Edit ─────────────────────────────────────────────────────────────────

    @GetMapping("/{slug}")
    public String edit(@PathVariable String slug, Model model,
                       @RequestParam(defaultValue = "0") int page) {
        GatewayInfo gateway = gatewayConfigService.getGateway(slug);
        model.addAttribute("gateway", gateway);
        model.addAttribute("payments",
                paymentRequestService.getByPlatform(slug,
                        page, 15));
        return "admin/business/configuration/payment-gateway-edit";
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @PostMapping("/{slug}/update")
    public String update(@PathVariable String slug,
                         @RequestParam Map<String, String> params,
                         RedirectAttributes redirect) {
        gatewayConfigService.updateFromParams(slug, params);
        redirect.addFlashAttribute("success", "Gateway configuration saved.");
        return "redirect:/admin/business/configuration/payment-gateways/" + slug;
    }

    // ── Toggle status ─────────────────────────────────────────────────────────

    @PostMapping("/{slug}/toggle")
    public String toggle(@PathVariable String slug, RedirectAttributes redirect) {
        gatewayConfigService.toggleStatus(slug);
        redirect.addFlashAttribute("success", "Gateway status updated.");
        return "redirect:/admin/business/configuration/payment-gateways";
    }
}

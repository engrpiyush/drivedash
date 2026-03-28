package com.drivedash.transaction.controller.web;

import com.drivedash.transaction.dto.WalletFundRequest;
import com.drivedash.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/transactions/wallet")
@RequiredArgsConstructor
public class CustomerWalletWebController {

    private final TransactionService transactionService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("transactions",
                transactionService.getWalletTransactions(search, page, 20));
        model.addAttribute("customers", transactionService.getActiveCustomers());
        model.addAttribute("req", new WalletFundRequest());
        model.addAttribute("search", search);
        return "admin/transaction/wallet";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") WalletFundRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("transactions",
                    transactionService.getWalletTransactions("", 0, 20));
            model.addAttribute("customers", transactionService.getActiveCustomers());
            model.addAttribute("search", "");
            return "admin/transaction/wallet";
        }
        try {
            transactionService.addWalletFund(req);
            ra.addFlashAttribute("success", "Wallet funded successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/transactions/wallet";
    }
}

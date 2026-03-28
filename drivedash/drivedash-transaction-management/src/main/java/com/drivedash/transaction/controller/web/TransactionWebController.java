package com.drivedash.transaction.controller.web;

import com.drivedash.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
public class TransactionWebController {

    private final TransactionService transactionService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String account,
            @RequestParam(defaultValue = "all") String attribute,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("transactions",
                transactionService.getPage(account, attribute, search, page, 20));
        model.addAttribute("account", account);
        model.addAttribute("attribute", attribute);
        model.addAttribute("search", search);
        return "admin/transaction/index";
    }
}

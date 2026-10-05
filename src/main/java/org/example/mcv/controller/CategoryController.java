package org.example.mcv.controller;

import org.example.mcv.entity.Category;
import org.example.mcv.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/categories";
    }

    @GetMapping("/categories")
    public String list(Model model) {
        model.addAttribute("categories", service.findAll());
        return "categories/list";
    }

    @GetMapping("/categories/new")
    public String createForm(@ModelAttribute("category") Category category) {
        return "categories/create";
    }

    @PostMapping("/categories")
    public String save(@ModelAttribute Category category) {
        service.create(category.getName());
        return "redirect:/categories";
    }
}

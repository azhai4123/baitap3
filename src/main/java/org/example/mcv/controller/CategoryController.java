package org.example.mcv.controller;

import org.example.mcv.entity.Category;
import org.example.mcv.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("categories", service.search(q));
        model.addAttribute("query", q == null ? "" : q);
        return "categories/list";
    }

    @GetMapping("/categories/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("editing", false);
        return "categories/form";
    }

    @PostMapping("/categories")
    public String save(@Valid @ModelAttribute("category") Category category, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return "categories/form";
        }
        try {
            service.create(category.getName());
        } catch (IllegalArgumentException exception) {
            result.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("editing", false);
            return "categories/form";
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", service.findById(id));
        model.addAttribute("editing", true);
        return "categories/form";
    }

    @PostMapping("/categories/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("category") Category category,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            return "categories/form";
        }
        try {
            service.update(id, category.getName());
        } catch (IllegalArgumentException exception) {
            result.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("editing", true);
            return "categories/form";
        }
        return "redirect:/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/categories";
    }
}

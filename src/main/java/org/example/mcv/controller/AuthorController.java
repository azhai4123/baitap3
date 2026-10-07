package org.example.mcv.controller;

import jakarta.validation.Valid;
import org.example.mcv.entity.Author;
import org.example.mcv.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @GetMapping("/authors")
    public String list(Model model) {
        model.addAttribute("authors", service.findAll());
        return "authors/list";
    }

    @GetMapping("/authors/new")
    public String createForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("editing", false);
        return "authors/form";
    }

    @PostMapping("/authors")
    public String create(@Valid @ModelAttribute("author") Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return "authors/form";
        }
        try {
            service.create(author.getName());
        } catch (IllegalArgumentException exception) {
            result.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("editing", false);
            return "authors/form";
        }
        return "redirect:/authors";
    }

    @GetMapping("/authors/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("author", service.findById(id));
        model.addAttribute("editing", true);
        return "authors/form";
    }

    @PostMapping("/authors/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("author") Author author,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            return "authors/form";
        }
        try {
            service.update(id, author.getName());
        } catch (IllegalArgumentException exception) {
            result.rejectValue("name", "duplicate", exception.getMessage());
            model.addAttribute("editing", true);
            return "authors/form";
        }
        return "redirect:/authors";
    }

    @PostMapping("/authors/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/authors";
    }
}
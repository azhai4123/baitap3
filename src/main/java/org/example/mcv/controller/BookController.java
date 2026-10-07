package org.example.mcv.controller;

import jakarta.validation.Valid;
import org.example.mcv.dto.BookForm;
import org.example.mcv.repository.AuthorRepository;
import org.example.mcv.repository.CategoryRepository;
import org.example.mcv.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {

    private final BookService bookService;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookController(BookService bookService, AuthorRepository authorRepository,
                          CategoryRepository categoryRepository) {
        this.bookService = bookService;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/books")
    public String list(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/books/new")
    public String createForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        model.addAttribute("editing", false);
        addLookups(model);
        return "books/form";
    }

    @PostMapping("/books")
    public String create(@Valid @ModelAttribute BookForm bookForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            addLookups(model);
            return "books/form";
        }
        try {
            bookService.create(bookForm);
        } catch (IllegalArgumentException exception) {
            result.reject("book.invalid", exception.getMessage());
            model.addAttribute("editing", false);
            addLookups(model);
            return "books/form";
        }
        return "redirect:/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("bookForm", bookService.findFormById(id));
        model.addAttribute("bookId", id);
        model.addAttribute("editing", true);
        addLookups(model);
        return "books/form";
    }

    @PostMapping("/books/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute BookForm bookForm,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("bookId", id);
            model.addAttribute("editing", true);
            addLookups(model);
            return "books/form";
        }
        try {
            bookService.update(id, bookForm);
        } catch (IllegalArgumentException exception) {
            result.reject("book.invalid", exception.getMessage());
            model.addAttribute("bookId", id);
            model.addAttribute("editing", true);
            addLookups(model);
            return "books/form";
        }
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    private void addLookups(Model model) {
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
    }
}
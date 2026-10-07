package org.example.mcv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Author name is required")
    @Size(max = 120, message = "Author name must be at most 120 characters")
    private String name;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Book> books = new LinkedHashSet<>();

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
}
package org.example.mcv.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(nullable = false)
    @NotBlank
    private String isbn;

    @Column(name = "author", nullable = false)
    @NotBlank
    private String authorName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "detail_id", unique = true)
    private BookDetail detail;

    @ManyToMany(fetch = FetchType.LAZY)
    @jakarta.persistence.JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new LinkedHashSet<>();

    public Book() {
    }

    public Book(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        if (this.author == author) {
            return;
        }
        if (this.author != null) {
            this.author.getBooks().remove(this);
        }
        this.author = author;
        this.authorName = author == null ? null : author.getName();
        if (author != null) {
            author.getBooks().add(this);
        }
    }

    public BookDetail getDetail() {
        return detail;
    }

    public void setDetail(BookDetail detail) {
        this.detail = detail;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }
}
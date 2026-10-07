package org.example.mcv.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class BookForm {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Select an author")
    private Long authorId;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotNull
    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount = 1;

    private Set<Long> categoryIds = new LinkedHashSet<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(categoryIds);
    }
}
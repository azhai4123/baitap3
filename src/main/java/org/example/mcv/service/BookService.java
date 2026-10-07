package org.example.mcv.service;

import jakarta.transaction.Transactional;
import org.example.mcv.dto.BookForm;
import org.example.mcv.entity.Author;
import org.example.mcv.entity.Book;
import org.example.mcv.entity.BookDetail;
import org.example.mcv.entity.Category;
import org.example.mcv.repository.AuthorRepository;
import org.example.mcv.repository.BookRepository;
import org.example.mcv.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public List<Book> findAll() {
        return bookRepository.findAllWithAssociations();
    }

    @Transactional
    public BookForm findFormById(Long id) {
        Book book = findById(id);
        BookForm form = new BookForm();
        form.setTitle(book.getTitle());
        form.setIsbn(book.getIsbn());
        form.setAuthorId(book.getAuthor().getId());
        if (book.getDetail() != null) {
            form.setPageCount(book.getDetail().getPageCount());
        }
        form.setCategoryIds(book.getCategories().stream().map(Category::getId).collect(Collectors.toSet()));
        return form;
    }

    @Transactional
    public Book create(BookForm form) {
        return save(new Book(), form);
    }

    @Transactional
    public Book update(Long id, BookForm form) {
        return save(findById(id), form);
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.delete(findById(id));
    }

    private Book save(Book book, BookForm form) {
        Author author = authorRepository.findById(form.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Selected author no longer exists"));
        Set<Long> categoryIds = form.getCategoryIds();
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("One or more selected categories no longer exist");
        }

        book.setTitle(form.getTitle().trim());
        book.setIsbn(form.getIsbn().trim());
        book.setAuthor(author);
        book.setAuthorName(author.getName());
        if (book.getDetail() == null) {
            book.setDetail(new BookDetail());
        }
        book.getDetail().setPageCount(form.getPageCount());
        for (Category category : new LinkedHashSet<>(book.getCategories())) {
            book.removeCategory(category);
        }
        categories.forEach(book::addCategory);
        return bookRepository.save(book);
    }

    private Book findById(Long id) {
        return bookRepository.findWithAssociationsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }
}
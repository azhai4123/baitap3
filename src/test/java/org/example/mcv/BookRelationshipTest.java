package org.example.mcv;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.example.mcv.entity.Author;
import org.example.mcv.entity.Book;
import org.example.mcv.entity.BookDetail;
import org.example.mcv.entity.Category;
import org.example.mcv.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRelationshipTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void mapsBookRelationshipsAndSupportsLazyAndEagerFetching() {
        Author author = new Author("Nguyen Nhat Anh");
        Category category = new Category("Novel");
        Book book = new Book("Mat biec");
        book.setIsbn("9786041234567");
        book.setDetail(new BookDetail(300));
        author.addBook(book);
        book.addCategory(category);

        entityManager.persist(author);
        entityManager.persist(category);
        entityManager.persist(book);
        entityManager.flush();
        Long bookId = book.getId();
        entityManager.clear();

        PersistenceUnitUtil persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        Book lazyBook = bookRepository.findById(bookId).orElseThrow();

        assertThat(persistenceUnitUtil.isLoaded(lazyBook, "author")).isFalse();
        assertThat(persistenceUnitUtil.isLoaded(lazyBook, "detail")).isTrue();
        assertThat(persistenceUnitUtil.isLoaded(lazyBook, "categories")).isFalse();

        entityManager.clear();
        Book fetchedBook = bookRepository.findWithAssociationsById(bookId).orElseThrow();

        assertThat(fetchedBook.getAuthor().getName()).isEqualTo("Nguyen Nhat Anh");
        assertThat(fetchedBook.getCategories()).extracting(Category::getName).containsExactly("Novel");
        assertThat(fetchedBook.getIsbn()).isEqualTo("9786041234567");
        assertThat(persistenceUnitUtil.isLoaded(fetchedBook, "author")).isTrue();
        assertThat(persistenceUnitUtil.isLoaded(fetchedBook, "categories")).isTrue();
    }
}
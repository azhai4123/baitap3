package org.example.mcv;

import org.example.mcv.entity.Author;
import org.example.mcv.entity.Book;
import org.example.mcv.entity.Category;
import org.example.mcv.repository.AuthorRepository;
import org.example.mcv.repository.BookRepository;
import org.example.mcv.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryCrudMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void rendersViewsAndSupportsCategoryAuthorAndBookCrud() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Categories")));
        mockMvc.perform(post("/authors").param("name", "Test Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));
        Author author = authorRepository.findByNameIgnoreCase("Test Author").orElseThrow();
        mockMvc.perform(get("/authors/{id}/edit", author.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Author")));
        mockMvc.perform(post("/authors/{id}", author.getId()).param("name", "Updated Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));
        author = authorRepository.findById(author.getId()).orElseThrow();
        assertThat(author.getName()).isEqualTo("Updated Author");

        mockMvc.perform(post("/categories").param("name", "Fiction"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
        Category fiction = categoryRepository.findByName("Fiction").orElseThrow();

        mockMvc.perform(post("/categories/{id}", fiction.getId()).param("name", "Novels"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"));
        fiction = categoryRepository.findById(fiction.getId()).orElseThrow();
        assertThat(fiction.getName()).isEqualTo("Novels");
        mockMvc.perform(get("/categories").param("q", "nov"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Novels")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Classics"))));

        mockMvc.perform(get("/categories/{id}/edit", fiction.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Novels")));

        mockMvc.perform(post("/categories").param("name", "Classics"))
                .andExpect(status().is3xxRedirection());
        Category classics = categoryRepository.findByName("Classics").orElseThrow();

        mockMvc.perform(post("/books")
                        .param("title", "Invalid Book")
                        .param("authorId", author.getId().toString())
                        .param("isbn", "9780000000000")
                        .param("pageCount", "200")
                        .param("categoryIds", "999"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("One or more selected categories no longer exist")));

        mockMvc.perform(post("/books")
                        .param("title", "Test Book")
                        .param("authorId", author.getId().toString())
                        .param("isbn", "9780000000001")
                        .param("pageCount", "240")
                        .param("categoryIds", fiction.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
        Book book = bookRepository.findAllWithAssociations().get(0);
        assertThat(book.getAuthorName()).isEqualTo("Updated Author");

        var blockedAuthorDelete = mockMvc.perform(post("/authors/{id}/delete", author.getId()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertThat(authorRepository.existsById(author.getId())).isTrue();
        assertThat(blockedAuthorDelete.getFlashMap().get("error")).isNotNull();

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Book")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Updated Author")));
        mockMvc.perform(get("/books/{id}/edit", book.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("9780000000001")));

        mockMvc.perform(post("/books/{id}", book.getId())
                        .param("title", "Updated Book")
                        .param("authorId", author.getId().toString())
                        .param("isbn", "9780000000002")
                        .param("pageCount", "320")
                        .param("categoryIds", fiction.getId().toString(), classics.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        mockMvc.perform(post("/categories/{id}/delete", fiction.getId()))
                .andExpect(status().is3xxRedirection());
        Book updatedBook = bookRepository.findWithAssociationsById(book.getId()).orElseThrow();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Book");
        assertThat(updatedBook.getCategories()).extracting(Category::getName).containsExactly("Classics");

        mockMvc.perform(post("/books/{id}/delete", book.getId()))
                .andExpect(status().is3xxRedirection());
        assertThat(bookRepository.existsById(book.getId())).isFalse();

        mockMvc.perform(post("/authors/{id}/delete", author.getId()))
                .andExpect(status().is3xxRedirection());
        assertThat(authorRepository.existsById(author.getId())).isFalse();
    }
}
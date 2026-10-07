package org.example.mcv.service;

import jakarta.transaction.Transactional;
import org.example.mcv.entity.Author;
import org.example.mcv.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Author findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + id));
    }

    @Transactional
    public Author create(String name) {
        ensureNameAvailable(name, null);
        return repository.save(new Author(name.trim()));
    }

    @Transactional
    public Author update(Long id, String name) {
        Author author = findById(id);
        ensureNameAvailable(name, id);
        author.setName(name.trim());
        return author;
    }

    @Transactional
    public void delete(Long id) {
        Author author = findById(id);
        if (!author.getBooks().isEmpty()) {
            throw new IllegalArgumentException("Remove or reassign this author's books before deleting the author");
        }
        repository.delete(author);
    }

    private void ensureNameAvailable(String name, Long currentId) {
        repository.findByNameIgnoreCase(name.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new IllegalArgumentException("Author name already exists");
            }
        });
    }
}
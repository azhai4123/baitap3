package org.example.mcv.service;
import jakarta.transaction.Transactional;
import org.example.mcv.entity.Book;
import org.example.mcv.entity.Category;
import org.example.mcv.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }
    public List<Category> findAll() {
        return repository.findAll();
    }

    public List<Category> search(String name) {
        if (name == null || name.isBlank()) {
            return findAll();
        }
        return repository.findByNameContainingIgnoreCaseOrderByIdDesc(name.trim());
    }

    public Category findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Transactional
    public Category create(String name) {
        if (repository.findByName(name.trim()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists");
        }
        return repository.save(new Category(name.trim()));
    }

    @Transactional
    public Category update(Long id, String name) {
        Category category = findById(id);
        Optional<Category> existing = repository.findByName(name.trim());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Category name already exists");
        }
        category.setName(name.trim());
        return category;
    }

    @Transactional
    public void delete(Long id) {
        Category category = findById(id);
        for (Book book : new LinkedHashSet<>(category.getBooks())) {
            book.removeCategory(category);
        }
        repository.delete(category);
    }
}

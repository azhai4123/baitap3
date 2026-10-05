package org.example.mcv.service;
import jakarta.transaction.Transactional;
import org.example.mcv.entity.Category;
import org.example.mcv.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }
    public List<Category> findAll() {
        return repository.findAll();
    }
    @Transactional
    public Category create(String name) {
        if(repository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("ten the loai da ton tai");
        }
        Category category = new Category();
        category.setName(name);
        return repository.save(category);
    }
    @Transactional
    public Category update(Long id , String name) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("khong tim thay the loai voi id = " + id));
        category.setName(name);
        return category;
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

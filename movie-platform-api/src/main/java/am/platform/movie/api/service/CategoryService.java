package am.platform.movie.api.service;

import am.platform.movie.common.model.Category;
import am.platform.movie.common.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(
            String name,
            String parentCategoryId
    ) {
        Category category = new Category();
        category.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        category.setName(name);
        if (parentCategoryId != null) {
            Optional<Category> parentCategoryOptional = categoryRepository.findById(parentCategoryId);
            parentCategoryOptional.ifPresent(category::setParentCategory);
        }

        return categoryRepository.save(category);
    }


    public Category updateCategory(
            Category category,
            String name,
            String parentCategoryId
    ) {
        category.setName(name);
        if (parentCategoryId != null) {
            Optional<Category> parentCategoryOptional = categoryRepository.findById(parentCategoryId);
            parentCategoryOptional.ifPresent(category::setParentCategory);
        }
        return categoryRepository.save(category);
    }

    public Optional<Category> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public List<Category> findCategoriesByParent(Category category) {
        return categoryRepository.findAllByParentCategory(category);
    }


}

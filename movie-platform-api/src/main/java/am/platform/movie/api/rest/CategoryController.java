package am.platform.movie.api.rest;

import am.platform.movie.api.rest.dto.CategoryDto;
import am.platform.movie.api.rest.response.ResponseInfo;
import am.platform.movie.api.service.CategoryService;
import am.platform.movie.common.model.Category;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static am.platform.movie.api.rest.response.ResponseMessage.CATEGORY_NOT_FOUND;
import static am.platform.movie.api.rest.response.ResponseMessage.PARENT_CATEGORY_NOT_FOUND;

/**
 * @author mher13.02.94@gmail.com
 */

@RestController
@CrossOrigin
@RequestMapping("/api/private/v1/")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    public static class CategoryRequest {
        @NotBlank
        public String name;
        public String parentCategoryId;
    }


    @AllArgsConstructor
    public static class CategorySubcategoryResponse {
        public CategoryDto category;
        public List<CategoryDto> subCategories;
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "PARENT_CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 201, message = "", response = CategoryDto.class)
    })
    @ApiOperation(value = "API for create category,if send parent id then create subcategory under parent category else create parent category. Has authority - ADMIN ", authorizations = {@Authorization(value = "Bearer")})
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/category")
    public HttpEntity<?> createCategory(@RequestBody CategoryRequest request) {

        if (request.parentCategoryId != null) {
            Optional<Category> parentCategory = categoryService.findCategoryById(request.parentCategoryId);
            if (parentCategory.isEmpty()) {
                return ResponseEntity.status(404).body(ResponseInfo.createResponse(PARENT_CATEGORY_NOT_FOUND));
            }

        }
        Category category = categoryService.createCategory(request.name, request.parentCategoryId);

        return ResponseEntity.status(201).body(new CategoryDto(category));
    }

    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND,PARENT_CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = CategoryDto.class)
    })
    @ApiOperation(value = "API for update category.Has authority - ADMIN", authorizations = {@Authorization(value = "Bearer")})
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/category/{categoryId}")
    public HttpEntity<?> updateCategory(
            @RequestBody CategoryRequest request,
            @PathVariable String categoryId
    ) {

        Optional<Category> categoryOptional = categoryService.findCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(CATEGORY_NOT_FOUND));
        }


        if (request.parentCategoryId != null) {
            Optional<Category> parentCategory = categoryService.findCategoryById(request.parentCategoryId);
            if (parentCategory.isEmpty()) {
                return ResponseEntity.status(404).body(ResponseInfo.createResponse(PARENT_CATEGORY_NOT_FOUND));
            }

        }
        Category updatedCategory = categoryService.updateCategory(categoryOptional.get(), request.name, request.parentCategoryId);

        return ResponseEntity.status(200).body(new CategoryDto(updatedCategory));
    }

    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = CategoryDto.class)
    })
    @ApiOperation(value = "API for get category by id", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/category/{categoryId}")
    public HttpEntity<?> getCategory(@PathVariable String categoryId) {

        Optional<Category> categoryOptional = categoryService.findCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(CATEGORY_NOT_FOUND));
        }
        return ResponseEntity.status(200).body(new CategoryDto(categoryOptional.get()));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = CategorySubcategoryResponse.class)
    })
    @ApiOperation(value = "API for get all categories with their sub-categories", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/private/category/categories")
    public HttpEntity<?> getAllParentAndSubCategories() {

        return ResponseEntity.status(200).body(generateCategoryResponse());
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = CategoryDto.class)
    })
    @ApiOperation(value = "API for get sub-categories of specific categories", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/category/{categoryId}/subcategory")
    public HttpEntity<?> getSubCategory(@PathVariable String categoryId) {

        Optional<Category> categoryOptional = categoryService.findCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(CATEGORY_NOT_FOUND));
        }

        List<Category> childCategories = categoryService.findCategoriesByParent(categoryOptional.get());

        return ResponseEntity.status(200).body(childCategories.stream().map(CategoryDto::new).collect(Collectors.toList()));
    }


    public List<CategorySubcategoryResponse> generateCategoryResponse() {
        List<CategorySubcategoryResponse> responses = new ArrayList<>();
        List<Category> parentCategories = categoryService.findCategoriesByParent(null);
        parentCategories.forEach(category -> {
            List<Category> subCategories = categoryService.findCategoriesByParent(category);
            CategorySubcategoryResponse response = new CategorySubcategoryResponse(new CategoryDto(category),
                    subCategories.stream().map(CategoryDto::new).collect(Collectors.toList()));
            responses.add(response);
        });
        return responses;
    }


}

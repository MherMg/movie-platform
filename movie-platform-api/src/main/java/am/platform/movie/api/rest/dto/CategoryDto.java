package am.platform.movie.api.rest.dto;

import am.platform.movie.common.model.Category;
import lombok.Data;

/**
 * @author mher13.02.94@gmail.com
 */

@Data
public class CategoryDto {
    public String id;
    public String parentId;
    public String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getParentCategory() == null ? null : category.getParentCategory().getId();
    }

}

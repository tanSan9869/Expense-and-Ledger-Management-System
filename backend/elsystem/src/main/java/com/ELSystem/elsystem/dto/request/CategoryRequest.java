package com.ELSystem.elsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category Name is required")
    @Size(min=2,max = 50,message = "Category Name must be between 2 and 50 characters")
    private String name;

    @Size(max=50,message = "Description must be less than 50 characters")
    private String description;
}

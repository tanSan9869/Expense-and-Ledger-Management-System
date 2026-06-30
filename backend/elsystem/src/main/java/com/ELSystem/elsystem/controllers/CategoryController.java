package com.ELSystem.elsystem.controllers;

import com.ELSystem.elsystem.dto.request.CategoryRequest;
import com.ELSystem.elsystem.exception.DuplicateResourceException;
import com.ELSystem.elsystem.model.Category;
import com.ELSystem.elsystem.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAll(){
        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody @Valid CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return new ResponseEntity<>(categoryRepository.save(category), HttpStatus.CREATED);
    }
}

package com.company.service;

import com.company.dto.CategoryDTO;
import com.company.entity.CategoryEntity;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Value("${server.domain.name}")
    private String domainName;


    public CategoryDTO create(CategoryDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());

        try {
            categoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toDTO(entity);
    }

    public List<CategoryDTO> list() {
        List<CategoryDTO> list = new ArrayList<>();
        categoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .forEach(entity -> list.add(toDTO(entity)));
        return list;
    }

    public CategoryDTO update(String id, CategoryDTO dto) {
        CategoryEntity entity = getById(id);
        entity.setName(dto.getName());
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            categoryRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toDTO(entity);
    }

    public Boolean delete(String id) {
        CategoryEntity entity = getById(id);
        categoryRepository.delete(entity);
        return true;
    }

    public CategoryDTO get(String categoryId) {
        CategoryEntity entity = getById(categoryId);
        return toDTO(entity);
    }

    public CategoryEntity getById(String id) {
        return categoryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });
    }

    public CategoryDTO toDTO(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public String toOpenUrl(String id) {
        return domainName + "category/" + id;
    }
}

package com.company.service;

import com.company.dto.TagDTO;
import com.company.entity.TagEntity;
import com.company.enums.ProfileRole;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {


    private final TagRepository tagRepository;

    @Value("${server.domain.name}")
    private String domainName;


    public TagDTO create(TagDTO dto) {
        TagEntity entity = new TagEntity();
        entity.setName("#" + dto.getName());

        try {
            tagRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);
    }

    public PageImpl<TagDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<TagDTO> dtoList = new ArrayList<>();

        Page<TagEntity> entityPage = tagRepository.findAll(pageable);
        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public TagDTO update(String id, TagDTO dto) {
        TagEntity entity = get(id);
        entity.setName("#" + dto.getName());
        entity.setUpdatedDate(LocalDateTime.now());

        try {
            tagRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique Items!");
        }
        return toDTO(entity);

    }

    public Boolean delete(String id) {
        TagEntity entity = get(id);
        tagRepository.delete(entity);
        return true;
    }

    public TagEntity get(String id) {
        return tagRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });
    }

    public TagDTO toDTO(TagEntity entity) {
        TagDTO dto = new TagDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public String toSearchUrl(String id) {
        return domainName + "video/tag/" + id;
    }
}

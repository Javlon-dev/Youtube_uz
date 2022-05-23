package com.company.service;

import com.company.dto.AttachDTO;
import com.company.entity.AttachEntity;
import com.company.exception.AppBadRequestException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.AttachRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachService {

    private final AttachRepository attachRepository;

    @Value("${attach.upload.folder}")
    private String attachFolder;

    @Value("${server.domain.name}")
    private String domainName;


    public AttachDTO upload(MultipartFile file) {
        AttachEntity entity = new AttachEntity();
        String pathFolder = getDateFolder();

        File folder = new File(attachFolder + "/" + pathFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            String extension = getExtension(file.getOriginalFilename());

            byte[] bytes = file.getBytes();

            entity = saveAttach(entity, pathFolder, extension, file);

            Path url = Paths.get(folder.getAbsolutePath() + "/" + entity.getId() + "." + extension);

            Files.write(url, bytes);

            return toDTO(entity);
        } catch (IOException | RuntimeException e) {
            log.warn("Cannot Upload");
            delete(entity.getId().toString());
            throw new AppBadRequestException(e.getMessage());
        }
    }

    public PageImpl<AttachDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<AttachDTO> dtoList = new ArrayList<>();

        Page<AttachEntity> entityPage = attachRepository.findAll(pageable);
        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public byte[] open(String id) {
        byte[] data;

        AttachEntity entity = getById(id);
        String pathFolder = entity.getPath() + "/" + id + "." + entity.getExtension();

        try {
            Path path = Paths.get(attachFolder + "/" + pathFolder);
            data = Files.readAllBytes(path);
            return data;
        } catch (IOException e) {
            log.warn("Cannot Open");
            return new byte[0];
        }
    }

    public ResponseEntity<?> download(String id) {
        try {
            AttachEntity entity = getById(id);
            String path = entity.getPath() + "/" + id + "." + entity.getExtension();

            Path file = Paths.get(attachFolder + "/" + path);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + entity.getOriginalName() + "\"")
                        .body(resource);
            } else {
                log.warn("Cannot Read");
                throw new AppBadRequestException("Could not read the file!");
            }

        } catch (MalformedURLException e) {
            log.warn("Cannot Download");
            throw new AppBadRequestException("Error" + e.getMessage());
        }
    }

    public Boolean delete(String id) {
        AttachEntity entity = getById(id);

        File file = new File(attachFolder + "/" + entity.getPath() +
                "/" + entity.getId() + "." + entity.getExtension());

        if (file.delete()) {
            attachRepository.deleteById(entity.getId());
            return true;
        } else {
            log.warn("Cannot Read");
            attachRepository.deleteById(entity.getId());
            throw new AppBadRequestException("Could not read the file!");
        }
    }

    public AttachEntity getById(String id) {
        return attachRepository.findById(UUID.fromString(id)).orElseThrow(() -> {
            log.warn("Not found {}", id);
            return new ItemNotFoundException("Not found!");
        });
    }

    public AttachDTO toDTO(AttachEntity entity) {
        AttachDTO dto = new AttachDTO();
        dto.setId(entity.getId().toString());
        dto.setPath(entity.getPath());
        dto.setOriginalName(entity.getOriginalName());
        dto.setUrl(domainName + "attach/download/" + entity.getId());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public AttachEntity saveAttach(AttachEntity entity, String pathFolder, String extension, MultipartFile file) {
        entity.setPath(pathFolder);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setExtension(extension);
        entity.setFileSize(file.getSize());
        attachRepository.save(entity);
        return entity;
    }

    public String toOpenUrl(String id) {
        return domainName + "attach/open/" + id;
    }

    public String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    public String getDateFolder() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);

        return year + "/" + month + "/" + day;
    }
}

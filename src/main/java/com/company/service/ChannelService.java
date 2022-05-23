package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.ChannelAboutDTO;
import com.company.dto.ChannelDTO;
import com.company.dto.ProfileDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ChannelEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.SubscriptionEntity;
import com.company.enums.ChannelStatus;
import com.company.enums.NotificationType;
import com.company.enums.ProfileRole;
import com.company.enums.SubscriptionStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.repository.ChannelRepository;
import com.company.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelService {


    private final ChannelRepository channelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AttachService attachService;
    private final ProfileService profileService;

    @Value("${server.domain.name}")
    private String domainName;


    public ChannelDTO create(ChannelDTO dto, String profileId) {
        ChannelEntity entity = new ChannelEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(ChannelStatus.ACTIVE);
        entity.setProfileId(UUID.fromString(profileId));

        try {
            channelRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }

        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setProfileId(UUID.fromString(profileId));
        subscriptionEntity.setChannelId(entity.getId());
        subscriptionEntity.setStatus(SubscriptionStatus.ACTIVE);
        subscriptionEntity.setNotificationType(NotificationType.PERSONALIZED);

        try {
            subscriptionRepository.save(subscriptionEntity);
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toDTO(entity);
    }

    public ChannelDTO updateAbout(ChannelAboutDTO dto, String channelId, String profileId) {
        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setUpdatedDate(LocalDateTime.now());
        try {
            channelRepository.updateBio(dto.getName(), dto.getDescription(), LocalDateTime.now(), entity.getId());
        } catch (DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }
        return toDTO(entity);
    }

    public Boolean channelImage(String attachId, String channelId, String profileId) {
        AttachEntity attachEntity = attachService.getById(attachId);

        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        if (Optional.ofNullable(entity.getPhotoId()).isPresent()) {
            if (entity.getPhotoId().toString().equals(attachId)) {
                return true;
            }
            String oldAttach = entity.getPhotoId().toString();
            channelRepository.updatePhoto(UUID.fromString(attachId), UUID.fromString(channelId));
            attachService.delete(oldAttach);
            return true;
        }
        channelRepository.updatePhoto(UUID.fromString(attachId), UUID.fromString(channelId));
        return true;
    }


    public Boolean channelBanner(String attachId, String channelId, String profileId) {
        AttachEntity attachEntity = attachService.getById(attachId);

        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        if (Optional.ofNullable(entity.getBannerId()).isPresent()) {
            if (entity.getBannerId().toString().equals(attachId)) {
                return true;
            }
            String oldAttach = entity.getBannerId().toString();
            channelRepository.updateBanner(UUID.fromString(attachId), UUID.fromString(channelId));
            attachService.delete(oldAttach);
            return true;
        }
        channelRepository.updateBanner(UUID.fromString(attachId), UUID.fromString(channelId));
        return true;
    }

    public PageImpl<ChannelDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        List<ChannelDTO> dtoList = new ArrayList<>();

        Page<ChannelEntity> entityPage = channelRepository.findAll(pageable);

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public Boolean changeStatus(String channelId, String profileId) {
        ChannelEntity entity = getById(channelId);

        ProfileEntity profileEntity = profileService.getById(profileId);

        if (entity.getProfileId().toString().equals(profileId) || profileEntity.getRole().equals(ProfileRole.ADMIN)) {
            switch (entity.getStatus()) {
                case ACTIVE -> {
                    channelRepository.updateStatus(ChannelStatus.BLOCK, entity.getId());
                }
                case BLOCK -> {
                    channelRepository.updateStatus(ChannelStatus.ACTIVE, entity.getId());
                }
            }

            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public List<ChannelDTO> profileChannelList(String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        List<ChannelDTO> dtoList = new ArrayList<>();

        List<ChannelEntity> entityList = channelRepository.findAllByProfileId(profileEntity.getId(),
                Sort.by(Sort.Direction.ASC, "name"));

        entityList.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });
        return dtoList;
    }

    public Boolean delete(String channelId, String profileId) {
        ChannelEntity entity = getById(channelId);

        if (!entity.getProfileId().toString().equals(profileId)) {
            log.warn("Not access {}", profileId);
            throw new AppForbiddenException("Not access!");
        }

        channelRepository.updateVisible(entity.getId());

        if (Optional.ofNullable(entity.getBannerId()).isPresent()) {
            attachService.delete(entity.getBannerId().toString());
        }
        if (Optional.ofNullable(entity.getPhotoId()).isPresent()) {
            attachService.delete(entity.getPhotoId().toString());
        }

        return true;
    }

    public ChannelDTO get(String id) {
        ChannelEntity entity = getById(id);
        return toDTO(entity);
    }

    public ChannelEntity getById(String id) {
        return channelRepository.findByIdAndVisible(UUID.fromString(id), true)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public ChannelDTO toDTO(ChannelEntity entity) {
        ChannelDTO dto = new ChannelDTO();
        dto.setId(entity.getId().toString());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        if (Optional.ofNullable(entity.getPhotoId()).isPresent()) {
            AttachDTO attachDTO = new AttachDTO(attachService.toOpenUrl(entity.getPhotoId().toString()));
            dto.setPhoto(attachDTO);
        }
        if (Optional.ofNullable(entity.getBannerId()).isPresent()) {
            AttachDTO attachDTO = new AttachDTO(attachService.toOpenUrl(entity.getBannerId().toString()));
            dto.setBanner(attachDTO);
        }

        dto.setSubscribers(subscriptionRepository.channelSubscriptionCount(entity.getId()));

        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }

    public String toOpenUrl(String id) {
        return domainName + "channel/" + id;
    }


}

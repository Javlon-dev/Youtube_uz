package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.ChangeNotificationDTO;
import com.company.dto.ChannelDTO;
import com.company.dto.SubscriptionDTO;
import com.company.entity.ChannelEntity;
import com.company.entity.SubscriptionEntity;
import com.company.enums.NotificationType;
import com.company.enums.SubscriptionStatus;
import com.company.exception.AppBadRequestException;
import com.company.exception.AppForbiddenException;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.SubscriptionInfoMapper;
import com.company.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ChannelService channelService;
    private final AttachService attachService;


    public SubscriptionDTO create(SubscriptionDTO dto, String profileId) {
        ChannelEntity channelEntity = channelService.getById(dto.getChannelId());

        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setProfileId(UUID.fromString(profileId));
        entity.setChannelId(channelEntity.getId());
        entity.setStatus(SubscriptionStatus.ACTIVE);
        entity.setNotificationType(NotificationType.PERSONALIZED);

        try {
            subscriptionRepository.save(entity);
        } catch (
                DataIntegrityViolationException e) {
            log.warn("Unique {}", dto);
            throw new AppBadRequestException("Unique!");
        }

        return toDTOMapper(getById(entity.getId()));
    }

    public Boolean changeStatus(String channelId, String profileId) {
        SubscriptionEntity entity = getByChannelIdAndProfileId(UUID.fromString(channelId), UUID.fromString(profileId));

        if (entity.getProfileId().toString().equals(profileId)) {

            switch (entity.getStatus()) {
                case ACTIVE -> {
                    subscriptionRepository.updateStatus(SubscriptionStatus.BLOCK, entity.getId());
                }
                case BLOCK -> {
                    subscriptionRepository.updateStatus(SubscriptionStatus.ACTIVE, entity.getId());
                }
            }
            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public Boolean changeNotification(ChangeNotificationDTO dto, String profileId) {
        SubscriptionEntity entity = getByChannelIdAndProfileId(UUID.fromString(dto.getChannelId()), UUID.fromString(profileId));

        if (entity.getProfileId().toString().equals(profileId)) {

            subscriptionRepository.updateNotification(dto.getNotificationType(), entity.getId());
            return true;
        }
        log.warn("Not access {}", profileId);
        throw new AppForbiddenException("Not access!");
    }

    public List<SubscriptionDTO> subscriptionListByProfileId(String profileId) {
        return subscriptionRepository
                .findAllByProfileIdAndStatus(UUID.fromString(profileId), SubscriptionStatus.ACTIVE)
                .stream()
                .map(this::toDTOMapper)
                .toList();
    }

    public SubscriptionEntity getByChannelIdAndProfileId(UUID channelId, UUID profileId) {
        return subscriptionRepository
                .findByChannelIdAndProfileId(channelId, profileId)
                .orElseThrow(() -> {
                    log.warn("Not found {}", profileId);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public SubscriptionInfoMapper getById(UUID id) {
        return subscriptionRepository
                .findByIdMapper(id)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    throw new ItemNotFoundException("Not found!");
                });
    }

    public SubscriptionDTO toDTOMapper(SubscriptionInfoMapper mapper) {
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(mapper.getS_id().toString());
        dto.setNotificationType(mapper.getS_type());

        dto.setCreatedDate(mapper.getS_created_date());

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(mapper.getC_id().toString());
        channelDTO.setName(mapper.getC_name());
        if (Optional.ofNullable(mapper.getC_photo()).isPresent()) {
            channelDTO.setPhoto(new AttachDTO(attachService.toOpenUrl(mapper.getC_photo().toString())));
        }

        dto.setChannel(channelDTO);

        return dto;
    }

}

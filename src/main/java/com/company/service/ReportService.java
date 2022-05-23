package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.ProfileDTO;
import com.company.dto.ReportDTO;
import com.company.entity.ChannelEntity;
import com.company.entity.ProfileEntity;
import com.company.entity.ReportEntity;
import com.company.entity.VideoEntity;
import com.company.exception.ItemNotFoundException;
import com.company.mapper.ReportInfoMapper;
import com.company.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ChannelService channelService;
    private final VideoService videoService;
    private final AttachService attachService;
    private final ProfileService profileService;


    public ReportDTO create(ReportDTO dto, String profileId) {
        ReportEntity entity = new ReportEntity();

        switch (dto.getType()) {
            case VIDEO -> {
                VideoEntity videoEntity = videoService.getById(dto.getEntityId());

                entity.setType(dto.getType());
                entity.setEntityId(videoEntity.getId());
            }
            case CHANNEL -> {
                ChannelEntity channelEntity = channelService.getById(dto.getEntityId());

                entity.setType(dto.getType());
                entity.setEntityId(channelEntity.getId());
            }
        }
        entity.setContent(dto.getContent());
        entity.setProfileId(UUID.fromString(profileId));

        reportRepository.save(entity);

        return toDTO(getByIdMapper(entity.getId()));
    }

    public PageImpl<ReportDTO> reportListPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ReportDTO> dtoList = new ArrayList<>();

        Page<ReportInfoMapper> mapperPage = reportRepository.findAllMapper(pageable);

        mapperPage.forEach(mapper -> {
            dtoList.add(toDTO(mapper));
        });

        return new PageImpl<>(dtoList, pageable, mapperPage.getTotalElements());
    }

    public PageImpl<ReportDTO> reportListPaginationByProfileId(int page, int size, String profileId) {
        ProfileEntity profileEntity = profileService.getById(profileId);

        Pageable pageable = PageRequest.of(page, size);

        List<ReportDTO> dtoList = new ArrayList<>();

        Page<ReportInfoMapper> mapperPage = reportRepository
                .findAllByProfileIdMapper(profileEntity.getId(), pageable);

        mapperPage.forEach(mapper -> {
            dtoList.add(toDTO(mapper));
        });

        return new PageImpl<>(dtoList, pageable, mapperPage.getTotalElements());
    }

    public Boolean delete(String reportId){
        ReportEntity entity = getById(reportId);
        reportRepository.delete(entity);
        return true;
    }

    public ReportEntity getById(String id) {
        return reportRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });
    }

    public ReportInfoMapper getByIdMapper(UUID id) {
        return reportRepository
                .findByIdMapper(id)
                .orElseThrow(() -> {
                    log.warn("Not found {}", id);
                    return new ItemNotFoundException("Not Found!");
                });
    }

    public ReportDTO toDTO(ReportInfoMapper mapper) {
        ReportDTO dto = new ReportDTO();
        dto.setId(mapper.getR_id().toString());
        dto.setContent(mapper.getR_content());

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(mapper.getP_id().toString());
        profileDTO.setName(mapper.getP_name());
        profileDTO.setSurname(mapper.getP_surname());
        if (Optional.ofNullable(mapper.getP_photo()).isPresent()) {
            profileDTO.setImage(new AttachDTO(attachService.toOpenUrl(mapper.getP_photo().toString())));
        }

        dto.setProfileDTO(profileDTO);

        dto.setEntityId(mapper.getR_entity_id().toString());
        dto.setType(mapper.getR_type());

        return dto;
    }
}

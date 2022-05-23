package com.company.service;

import com.company.dto.AttachDTO;
import com.company.dto.AuthDTO;
import com.company.dto.ProfileDTO;
import com.company.dto.RegistrationDTO;
import com.company.entity.AttachEntity;
import com.company.entity.ProfileEntity;
import com.company.enums.EmailType;
import com.company.enums.ProfileRole;
import com.company.enums.ProfileStatus;
import com.company.exception.*;
import com.company.repository.ProfileRepository;
import com.company.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final AttachService attachService;


    @Value("${server.domain.name}")
    private String domainName;


    public ProfileDTO login(AuthDTO dto) {
        ProfileEntity entity = authorization(dto);

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(entity.getName());
        profileDTO.setSurname(entity.getSurname());
        profileDTO.setEmail(entity.getEmail());
        profileDTO.setJwt(JwtUtil.encode(entity.getId().toString(), entity.getRole()));

        Optional<AttachEntity> optional = Optional.ofNullable(entity.getAttach());
        if (optional.isPresent()) {
            AttachDTO attachDTO = new AttachDTO();
            attachDTO.setUrl(attachService.toOpenUrl(optional.get().getId().toString()));
            profileDTO.setImage(attachDTO);
        }

        return profileDTO;
    }

    public ProfileEntity authorization(AuthDTO dto) {
        ProfileEntity entity = getByEmail(dto.getEmail());

        if (entity.getStatus().equals(ProfileStatus.BLOCK)) {
            log.warn("Profile blocked {}", dto);
            throw new AppForbiddenException("You're blocked!\nPlease contact with Admin!");
        }
        if (!entity.getStatus().equals(ProfileStatus.ACTIVE)) {
            log.warn("No Access {}", dto);
            throw new AppForbiddenException("No Access!");
        }

        String password = DigestUtils.md5Hex(dto.getPassword());
        if (!entity.getPassword().equals(password)) {
            log.warn("Invalid Password {}", dto);
            throw new AppBadRequestException("Invalid Password!");
        }

        return entity;
    }

    public String registration(RegistrationDTO dto) {
        Optional<ProfileEntity> optional = profileRepository.findByEmailAndVisible(dto.getEmail(), true);
        ProfileEntity entity;
        if (optional.isPresent()) {
            entity = optional.get();

            if (!entity.getStatus().equals(ProfileStatus.INACTIVE)) {
                log.warn("Unique {}", dto.getEmail());
                throw new ItemAlreadyExistsException("This Email already used!");
            }

        } else {
            String password = DigestUtils.md5Hex(dto.getPassword());

            entity = new ProfileEntity();
            entity.setName(dto.getName());
            entity.setSurname(dto.getSurname());
            entity.setEmail(dto.getEmail());
            entity.setPassword(password);
            entity.setStatus(ProfileStatus.INACTIVE);
            entity.setRole(ProfileRole.USER);

            profileRepository.save(entity);
        }

        Thread thread = new Thread(() -> {
            try {
                sendEmail(entity, "/verification/", EmailType.VERIFICATION);
            } catch (AppBadRequestException e) {
                profileRepository.deleteById(entity.getId());
                throw new AppBadRequestException(e.getMessage());
            }
        });
        thread.start();

        return "Confirm your email address.\nCheck your email!";
    }

    public String verification(String id) {
        if (profileRepository.updateStatus(ProfileStatus.ACTIVE, UUID.fromString(id)) > 0) {
            return "Successfully verified";
        }
        log.warn("Unsuccessfully verified {}", id);
        throw new AppNotAcceptableException("Unsuccessfully verified!");
    }

    public void sendEmail(ProfileEntity entity, String domainPath, EmailType type) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h2>Hellomaleykum ").append(entity.getName()).append(" ").append(entity.getSurname()).append("!</h2>");
        builder.append("<br><p><b>To verify your registration click to next link -> ");
        builder.append("<a href=\"" + domainName + domainPath);
        switch (type) {
            case VERIFICATION -> builder.append(JwtUtil.encode(entity.getId().toString()));
            case RESET -> builder.append(JwtUtil.encodeEmail(entity.getId().toString(), entity.getEmail(), entity.getRole()));
        }
        builder.append("\">This Link</a></b></p></br>");
        builder.append("<br>Mazgi !</br>");
        emailService.send(entity.getEmail(), "Active Your Email", builder.toString(), type);
    }

    public ProfileEntity getByEmail(String email) {
        return profileRepository
                .findByEmailAndVisible(email, true)
                .orElseThrow(() -> {
                    log.warn("Not found {}", email);
                    return new ItemNotFoundException("Not found!");
                });
    }
}

package com.company.repository;

import com.company.entity.ProfileEntity;
import com.company.enums.ProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findByEmailAndVisible(String email, Boolean visible);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set status = :status where id = :id")
    int updateStatus(@Param("status") ProfileStatus status, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set attachId = :attachId where id = :id")
    int updateAttach(@Param("attachId") UUID attachId, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set email = :email where id = :id")
    int updateEmail(@Param("email") String email, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set name = :name , surname = :surname , updatedDate = :now where id = :id")
    int updateBio(@Param("name") String name, @Param("surname") String surname,
                  @Param("now") LocalDateTime now, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set password = :password where id = :id")
    int updatePassword(@Param("password") String password, @Param("id") UUID id);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set visible = false where id = :id")
    void updateVisible(@Param("id") UUID id);
}
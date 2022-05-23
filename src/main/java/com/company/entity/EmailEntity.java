package com.company.entity;

import com.company.enums.EmailType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "email_history")
@Getter
@Setter
public class EmailEntity extends BaseEntity {

    @Column(nullable = false)
    private String toEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailType type;
}

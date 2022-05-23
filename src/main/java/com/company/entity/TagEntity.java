package com.company.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag")
@Getter
@Setter
public class TagEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

}

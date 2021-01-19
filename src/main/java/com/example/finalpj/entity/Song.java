package com.example.finalpj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class Song {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;
//    private String description;
    private BigDecimal price;
//    private Integer listenCount = 0;
    private boolean status = true;

    private String main;
    private String demo;

    @UpdateTimestamp
    private Date updateAt;
    @CreationTimestamp
    private Date createAt;

    @ManyToOne
    @JsonIgnoreProperties("songs")
    private User creator;

    @ManyToOne
    @JsonIgnoreProperties("songs")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("song")
    private Transaction transaction;
}

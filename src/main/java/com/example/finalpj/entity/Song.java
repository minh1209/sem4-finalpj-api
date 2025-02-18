package com.example.finalpj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotBlank
    private String name;
    private String description;
    private Integer bpm;
    private BigDecimal price;
    private String main;
    private String demo;
    private Boolean status = false;

    @UpdateTimestamp
    private Date updateAt;
    @CreationTimestamp
    private Date createAt;

    @ManyToOne
    @JsonIgnoreProperties({"songs", "transactions","roles","token"})
    private User creator;

    @ManyToOne
    @JsonIgnoreProperties("songs")
    private Category category;

    @OneToMany(mappedBy = "song")
    @JsonIgnoreProperties("song")
    @OrderBy("authorPayment asc , createAt desc")
    private List<Transaction> transactions;
}

package com.example.finalpj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotBlank
    @Email
    private String email;

    @Size(min = 6)
    @Column(length = 10485760)
    private String password;

    @Size(min = 1, max = 32)
    private String username;

    @DateTimeFormat
    private Date dob;
    private String phone;
    private String identification;
    private Boolean active = false;
    private String avatar;

    @UpdateTimestamp
    private Date updateAt;
    @CreationTimestamp
    private Date createAt;

    @ManyToMany
    @JoinTable(name = "user_role")
    @JsonIgnoreProperties("users")
    private List<Role> roles;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("creator")
    @OrderBy("createAt desc")
    private List<Song> songs;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("customer")
    @OrderBy("createAt desc")
    private List<Transaction> transactions;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private Token token;
}

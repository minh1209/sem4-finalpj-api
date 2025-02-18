package com.example.finalpj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String payment;
    private Boolean authorPayment = false;

    @CreationTimestamp
    private Date createAt;

    @UpdateTimestamp
    private Date updateAt;

    @ManyToOne
    @JsonIgnoreProperties("transactions")
    private Song song;

    @ManyToOne
    @JsonIgnoreProperties({"transactions", "songs"})
    private User customer;

    @OneToOne
    @JsonIgnoreProperties({"transactions", "songs", "token", "roles"})
    private User handler;

}

package com.example.finalpj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String payment;

    @CreationTimestamp
    private Date createAt;

    @OneToOne
    @JsonIgnoreProperties("transaction")
    private Song song;

    @ManyToOne
    @JsonIgnoreProperties({"transactions", "songs"})
    private User customer;
}

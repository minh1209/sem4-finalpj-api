package com.example.finalpj.dto;

import java.util.Date;

public interface TransactionDto {
    String getId();
    String getPayment();
    Boolean getAuthorPayment();
    Date getCreateAt();
    SongChildrenDto getSong();
    UserChildrenDto getCustomer();
}

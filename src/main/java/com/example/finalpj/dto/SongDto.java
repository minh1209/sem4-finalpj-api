package com.example.finalpj.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface SongDto {
    String getId();
    String getName();
    String getDescription();
    Integer getBpm();
    BigDecimal getPrice();
    String getDemo();
    String getMain();
    Date getCreateAt();
    UserChildrenDto getCreator();
    CategoryDto getCategory();
    List<TransactionChildrenDto> getTransactions();
}

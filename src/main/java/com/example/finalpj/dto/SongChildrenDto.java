package com.example.finalpj.dto;

import java.math.BigDecimal;

public interface SongChildrenDto {
    String getId();
    String getName();
    String getDemo();
    String getMain();
    BigDecimal getPrice();
    CategoryDto getCategory();
    UserChildrenDto getCreator();
}

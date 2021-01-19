package com.example.finalpj.mapper;

import com.example.finalpj.dto.SongDTO;
import com.example.finalpj.entity.Song;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Mapper

public interface SongMapper {
    SongDTO toSongDTO(Song song);
    List<SongDTO> toSongDTOs(List<Song> songs);
}

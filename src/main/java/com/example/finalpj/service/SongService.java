package com.example.finalpj.service;

import com.example.finalpj.dto.SongDto;
import com.example.finalpj.entity.Category;
import com.example.finalpj.entity.Song;
import com.example.finalpj.entity.User;
import com.example.finalpj.reposiroty.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    //    Non-DTO
    public Song save(Song s) {
        return songRepository.save(s);
    }

    public Optional<Song> findById(String id) {
        return songRepository.findById(id);
    }

    //    DTO
    public List<SongDto> findAllDto() {
        return songRepository.findAllDto();
    }

    public List<SongDto> findAllDtoByCreator(String creator_id) {
        return songRepository.findAllDtoByCreator(creator_id);
    }

    public SongDto findDtoById(String id) {
        return songRepository.findDtoById(id);
    }

    public List<SongDto> findAllDtoByCategory(String category_id) {
        return songRepository.findAllDtoByCategory(category_id);
    }

    public List<SongDto> findAllDtoByCreatorAndCategory(String creator_id, String category_id) {
        return songRepository.findAllDtoByCreatorAndCategory(creator_id, category_id);
    }

    public List<SongDto> findAllDtoByCreatorAndCategoryAndTime(String creator_id, String category_id, Date start, Date end) {
        return songRepository.findAllDtoByCreatorAndCategoryAndTime(creator_id, category_id, start, end);
    }

    public List<SongDto> findAllDtoBySearchName(String name) {
        return songRepository.findAllDtoBySearchName(name);
    }

    public List<SongDto> findallDtoByCategoryAndTime(String category_id, Date start, Date end) {
        return songRepository.findallDtoByCategoryAndTime(category_id, start, end);
    }

    public List<SongDto> findallDtoByCreatorAndTime(String creator_id, Date start, Date end) {
        return songRepository.findallDtoByCreatorAndTime(creator_id, start, end);
    }

    public List<SongDto> findTop6NewestDto() {
        Pageable pageable = PageRequest.of(0, 6);
        return songRepository.findTop6NewestDto(pageable);
    }
}

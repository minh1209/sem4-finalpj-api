package com.example.finalpj.service;

import com.example.finalpj.entity.Song;
import com.example.finalpj.reposiroty.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    public Song save(Song s) {
        return songRepository.save(s);
    }

//    public List<Song> findAll(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("create_at").descending());
//        return songRepository.findAll(pageable).getContent();
//    }

    public List<Song> findAllByOrderByCreateAtDesc() {
        return songRepository.findAllByOrderByCreateAtDesc();
    }

    public Optional<Song> findById(String id) {
        return songRepository.findById(id);
    }

    public List<Song> findTop6ByOrderByCreate_atDesc() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        return songRepository.findAll(pageable).getContent();
    }
}

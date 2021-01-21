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

    public List<Song> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAll(pageable).getContent();
    }

    public Optional<Song> findById(String id) {
        return songRepository.findById(id);
    }

    public List<Song> findTop6ByStatusOrderByCreateAtDesc() {
        return songRepository.findTop6ByStatusOrderByCreateAtDesc(true);
    }

    @Transactional
    public void deleteAllByCreatorId(String id) {
        songRepository.deleteAllByCreatorId(id);
    }
}

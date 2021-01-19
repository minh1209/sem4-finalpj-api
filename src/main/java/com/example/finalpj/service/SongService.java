package com.example.finalpj.service;

import com.example.finalpj.dto.SongDTO;
import com.example.finalpj.entity.Song;
import com.example.finalpj.mapper.SongMapper;
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
//    @Autowired
//    private SongMapper songMapper;

    public Song save(Song s) {
        return songRepository.save(s);
    }

    public List<Song> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAll(pageable).getContent();
    }

    public List<Song> findAllByStatus(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAllByStatus(true,pageable);
//        return songMapper.toSongDTOs(songRepository.findAllByStatus(true,pageable));
    }

    public List<Song> findAllByCreatorId(int page, int size, String id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAllByCreatorId(id, pageable);
    }

    public List<Song> findAllByCreatorIdAndStatus(int page, int size, String id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAllByCreatorIdAndStatus(id, true,pageable);
    }

    public List<Song> findAllByCategoryIdAndStatus(int page, int size, String id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return songRepository.findAllByCategoryIdAndStatus(id, true,pageable);
    }

    public Optional<Song> findById(String id) {
        return songRepository.findById(id);
    }

    public List<Song> findTop10ByOrderByCreateAtDesc() {
        return songRepository.findTop10ByOrderByCreateAtDesc();
    }

    @Transactional
    public void deleteAllByCreatorId(String id) {
        songRepository.deleteAllByCreatorId(id);
    }
}

package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findAllByCreatorId(String id, Pageable pageable);
    void deleteAllByCreatorId(String id);
//    void deleteById(String id);
    List<Song> findTop6ByStatusOrderByCreateAtDesc(Boolean status);
}

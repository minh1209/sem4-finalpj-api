package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Category;
import com.example.finalpj.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findAllByCreatorId(String id, Pageable pageable);
    List<Song> findAllByOrderByCreateAtDesc();
//    void deleteById(String id);
    List<Song> findAllByCategory(Category category, Pageable pageable);
}

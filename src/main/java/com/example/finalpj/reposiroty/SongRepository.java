package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Category;
import com.example.finalpj.entity.Song;
import com.example.finalpj.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    List<Song> findAllByCreatorId(String id, Pageable pageable);
    List<Song> findAllByOrderByCreateAtDesc();
//    void deleteById(String id);
    List<Song> findAllByCategory_Name(String category, Pageable pageable);

    @Query(value = "select s from Song s where lower(trim(s.name)) like %?1% order by s.createAt desc")
    List<Song> findAllByNameLike(String name);
}

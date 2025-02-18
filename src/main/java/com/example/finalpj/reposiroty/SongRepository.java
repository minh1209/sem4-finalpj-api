package com.example.finalpj.reposiroty;

import com.example.finalpj.dto.SongDto;
import com.example.finalpj.entity.Category;
import com.example.finalpj.entity.Song;
import com.example.finalpj.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, String> {
    //    void deleteById(String id);
    List<Song> findAllByOrderByCreateAtDesc();

    //    DTO
    @Query(value = "select s from Song s order by s.createAt desc")
    List<SongDto> findAllDto();

    @Query(value = "select s from Song s where s.status = true order by s.createAt desc")
    List<SongDto> findAllDtoStatusTrue();

    @Query(
            value = "select s from Song s where s.status = true order by s.createAt desc",
            countQuery = "select count(s) from Song s order by s.createAt desc"
    )
    List<SongDto> findAllDtoPage(Pageable pageable);


    @Query(value = "select s from Song s where s.id = ?1 order by s.createAt desc")
    SongDto findDtoById(String id);


    @Query(value = "select s from Song s where s.creator.id = ?1 and s.status = true order by s.createAt desc")
    List<SongDto> findAllDtoByCreator(String creator_id);

    @Query(value = "select s from Song s where s.creator.id = ?1 order by s.createAt desc")
    List<SongDto> findAllDtoByCreatorOwner(String creator_id);

    @Query(
            value = "select s from Song s where s.creator.id = ?1 and s.status = true order by s.createAt desc",
            countQuery = "select count(s) from Song s where s.creator.id = ?1 order by s.createAt desc"
    )
    List<SongDto> findAllDtoByCreatorPage(String creator_id, Pageable pageable);


    @Query(value = "select s from Song s where s.category.id = ?1 and s.status = true order by s.createAt desc")
    List<SongDto> findAllDtoByCategory(String category_id);

    @Query(
            value = "select s from Song s where s.category.id = ?1 and s.status = true order by s.createAt desc",
            countQuery = "select count(s) from Song s where s.category.id = ?1 order by s.createAt desc"
    )
    List<SongDto> findAllDtoByCategoryPage(String category_id, Pageable pageable);


    @Query(value = "select s from Song s where s.creator.id = ?1 and s.category.id = ?2 and s.status = true order by s.createAt desc")
    List<SongDto> findAllDtoByCreatorAndCategory(String creator_id, String category_id);

    @Query(
            value = "select s from Song s where s.creator.id = ?1 and s.category.id = ?2 and s.status = true order by s.createAt desc",
            countQuery = "select count(s) from Song s where s.creator.id = ?1 and s.category.id = ?2 order by s.createAt desc"
    )
    List<SongDto> findAllDtoByCreatorAndCategoryPage(String creator_id, String category_id, Pageable pageable);


    @Query(value = "select s from Song s where lower(trim(s.name)) like %?1% and s.status = true order by s.createAt desc")
    List<SongDto> findAllDtoBySearchName(String name);


    @Query(value = "select s from Song s inner join Transaction t on s.id = t.song.id where s.category.id = ?1 " +
            "and (t.createAt between ?2 and ?3) and s.status = true order by s.createAt desc")
    List<SongDto> findallDtoByCategoryAndTransactionTime(String category_id, Date start, Date end);


    @Query(
            value = "select s from Song s where s.status = true order by s.createAt desc",
            countQuery = "select count(s) from Song s"
    )
    List<SongDto> findTop6NewestDto(Pageable pageable);


    //    Admin
    @Query(value = "select s from Song s where (s.creator.id = ?1 and s.category.id = ?2) and (s.createAt between ?3 and ?4) order by s.createAt desc")
    List<SongDto> findAllDtoByCreatorAndCategoryAndTime(String creator_id, String category_id, Date start, Date end);

    @Query(value = "select s from Song s where s.category.id = ?1 " +
            "and (s.createAt between ?2 and ?3) order by s.createAt desc")
    List<SongDto> findallDtoByCategoryAndTime(String category_id, Date start, Date end);

    @Query(value = "select s from Song s where s.creator.id = ?1 " +
            "and (s.createAt between ?2 and ?3) order by s.createAt desc")
    List<SongDto> findallDtoByCreatorAndTime(String creator_id, Date start, Date end);

    @Query(value = "select s from Song s where (s.createAt between ?1 and ?2) order by s.createAt desc")
    List<SongDto> findallDtoByTime(Date start, Date end);
}

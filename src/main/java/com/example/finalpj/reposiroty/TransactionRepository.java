package com.example.finalpj.reposiroty;

import com.example.finalpj.dto.TransactionChildrenDto;
import com.example.finalpj.dto.TransactionDto;
import com.example.finalpj.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByCreateAtBetweenOrderByCreateAtDesc(Date start, Date end);

    List<Transaction> findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(String creator_id, Date start, Date end);

    List<Transaction> findAllBySongIdAndCreateAtBetweenOrderByCreateAtDesc(String song_id, Date start, Date end);

    List<Transaction> findAllByCustomerIdAndCreateAtBetweenOrderByCreateAtDesc(String customer_id, Date start, Date end);

    Optional<Transaction> findBySongIdAndCustomerId(String song_id, String customer_id);

    List<Transaction> findAllByCreateAtBetween(Date start, Date end);

    //    DTO
    @Query(value = "select t from Transaction t where t.id = ?1")
    TransactionDto findDtoById(String id);

    @Query(value = "select t from Transaction t where t.createAt between ?1 and ?2 order by t.createAt desc")
    List<TransactionDto> findAllDtoByTime(Date start, Date end);


    @Query(value = "select t from Transaction t where t.song.creator.id = ?1 order by t.createAt desc")
    List<TransactionDto> findAllDtoByCreator(String creator_id);

    @Query(value = "select t from Transaction t where t.song.creator.id = ?1 and (t.createAt between ?2 and ?3) order by t.createAt desc")
    List<TransactionDto> findAllDtoByCreatorAndTime(String creator_id, Date start, Date end);


    @Query(value = "select t from Transaction t where t.song.id = ?1 order by t.createAt desc")
    List<TransactionDto> findAllDtoBySong(String song_id);

    @Query(value = "select t from Transaction t where t.song.id = ?1 and (t.createAt between ?2 and ?3) order by t.createAt desc")
    List<TransactionDto> findAllDtoBySongAndTime(String song_id, Date start, Date end);


    @Query(value = "select t from Transaction t where t.customer.id = ?1 order by t.createAt desc")
    List<TransactionDto> findAllDtoByCustomer(String customer_id);

    @Query(value = "select t from Transaction t where t.customer.id = ?1 and (t.createAt between ?2 and ?3) order by t.createAt desc")
    List<TransactionDto> findAllDtoByCustomerAndTime(String customer_id, Date start, Date end);


    @Query(value = "select t from Transaction t where t.customer.id = ?1 and t.song.id = ?2")
    Optional<TransactionChildrenDto> checkUserPurchasedSong(String customer_id, String song_id);
}

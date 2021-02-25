package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllBySongIdOrderByCreateAtDesc(String id, Pageable pageable);
    List<Transaction> findAllByCustomerIdOrderByCreateAtDesc(String id, Pageable pageable);
//    List<Transaction> findAllByCreateAtBetween(Date start, Date end, Pageable pageable);
    List<Transaction> findAllByCreateAtBetweenOrderByCreateAtDesc(Date start, Date end);
//    List<Transaction> findAllBySong_Creator_IdAndCreateAtBetween(String id, Date start, Date end, Pageable pageable);
    List<Transaction> findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(String id, Date start, Date end);
//    @Query(nativeQuery = true, value = "select t.*, s.name as song_name, s.price as song_price, s.main as song_main from transactions t inner join songs s on t.song_id = s.id inner join users u on t.customer_id = u.id where u.id = ?1")
//    List<TransactionDAO> findAllByCustomerIdCustom(String id, Pageable pageable);
    List<Transaction> findAllByOrderByCreateAtDesc(Pageable pageable);
    Optional<Transaction> findBySongIdAndCustomerId(String song_id, String customer_id);
}

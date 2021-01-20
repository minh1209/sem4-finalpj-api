package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllBySongId(String id, Pageable pageable);
    List<Transaction> findAllByCustomerId(String id, Pageable pageable);
    List<Transaction> findAllByCreateAtBetween(Date start, Date end, Pageable pageable);
    List<Transaction> findAllBySong_Creator_IdAndCreateAtBetween(String id, Date start, Date end, Pageable pageable);
//    @Query(nativeQuery = true, value = "select t.*, s.name as song_name, s.price as song_price, s.main as song_main from transactions t inner join songs s on t.song_id = s.id inner join users u on t.customer_id = u.id where u.id = ?1")
//    List<TransactionDAO> findAllByCustomerIdCustom(String id, Pageable pageable);

    void deleteAllByCustomerId(String id);
    void deleteById(String id);
}

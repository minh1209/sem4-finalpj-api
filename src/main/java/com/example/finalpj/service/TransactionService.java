package com.example.finalpj.service;

import com.example.finalpj.entity.Transaction;
import com.example.finalpj.reposiroty.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

//    public List<Transaction> findAllBySongId(int page, int size, String id) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("create_at").descending());
//        return transactionRepository.findAllBySongId(id, pageable);
//    }

    public List<Transaction> findAllBySongIdOrderByCreateAtDesc(String id) {
        return transactionRepository.findAllBySongIdOrderByCreateAtDesc(id);
    }

//    public List<Transaction> findAllByCustomerId(int page, int size, String id) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("create_at").descending());
//        return transactionRepository.findAllByCustomerId(id, pageable);
//    }

    public List<Transaction> findAllByCustomerIdOrderByCreateAtDesc(String id) {
        return transactionRepository.findAllByCustomerIdOrderByCreateAtDesc(id);
    }

//    public List<Transaction> findAll(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("create_at").descending());
//        return transactionRepository.findAll(pageable).getContent();
//    }

    public List<Transaction> findAll() {
        return transactionRepository.findAllByOrderByCreateAtDesc();
    }

//    public List<Transaction> findAllByCreateAtBetween(int page, int size, Date start, Date end) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
//        return transactionRepository.findAllByCreateAtBetween(start, end, pageable);
//    }
    public List<Transaction> findAllByCreateAtBetweenOrderByCreateAtDesc(Date start, Date end) {
        return transactionRepository.findAllByCreateAtBetweenOrderByCreateAtDesc(start, end);
    }

//    public List<Transaction> findAllBySong_Creator_IdAndCreateAtBetween(int page, int size, Date start, Date end, String id) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
//        return transactionRepository.findAllBySong_Creator_IdAndCreateAtBetween(id, start, end, pageable);
//    }
    public List<Transaction> findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(Date start, Date end, String id) {
        return transactionRepository.findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(id, start, end);
    }

    public Transaction save(Transaction t) {
        return transactionRepository.save(t);
    }
}

package com.example.finalpj.service;

import com.example.finalpj.entity.Transaction;
import com.example.finalpj.reposiroty.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAllBySongIdOrderByCreateAtDesc(String id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllBySongIdOrderByCreateAtDesc(id, pageable);
    }

    public List<Transaction> findAllByCustomerIdOrderByCreateAtDesc(String id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllByCustomerIdOrderByCreateAtDesc(id, pageable);
    }

    public List<Transaction> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllByOrderByCreateAtDesc(pageable);
    }

    public List<Transaction> findAllByCreateAtBetweenOrderByCreateAtDesc(Date start, Date end, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllByCreateAtBetweenOrderByCreateAtDesc(start, end, pageable);
    }

    public List<Transaction> findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(Date start, Date end, String id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(id, start, end, pageable);
    }

    public Transaction save(Transaction t) {
        return transactionRepository.save(t);
    }
}

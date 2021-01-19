package com.example.finalpj.service;

import com.example.finalpj.entity.Transaction;
import com.example.finalpj.reposiroty.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAllBySongId(int page, int size, String id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return transactionRepository.findAllBySongId(id, pageable);
    }

    public List<Transaction> findAllByCustomerId(int page, int size, String id) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return transactionRepository.findAllByCustomerId(id, pageable);
    }

    public List<Transaction> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return transactionRepository.findAll(pageable).getContent();
    }

    public List<Transaction> findAllByCreateAtBetween(int page, int size, Date start, Date end) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        return transactionRepository.findAllByCreateAtBetween(start, end, pageable);
    }

    public Transaction save(Transaction t) {
        return transactionRepository.save(t);
    }

    @Transactional
    public void deleteAllByCustomerId(String id) { transactionRepository.deleteAllByCustomerId(id); }
}

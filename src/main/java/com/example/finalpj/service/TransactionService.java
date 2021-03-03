package com.example.finalpj.service;

import com.example.finalpj.dto.TransactionDto;
import com.example.finalpj.entity.Transaction;
import com.example.finalpj.reposiroty.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAllByCreateAtBetweenOrderByCreateAtDesc(Date start, Date end) {
        return transactionRepository.findAllByCreateAtBetweenOrderByCreateAtDesc(start, end);
    }

    public List<Transaction> findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(String creator_id, Date start, Date end) {
        return transactionRepository.findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(creator_id, start, end);
    }

    public List<Transaction> findAllBySongIdAndCreateAtBetweenOrderByCreateAtDesc(String song_id, Date start, Date end) {
        return transactionRepository.findAllBySongIdAndCreateAtBetweenOrderByCreateAtDesc(song_id, start, end);
    }

    public List<Transaction> findAllByCustomerIdAndCreateAtBetweenOrderByCreateAtDesc(String customer_id, Date start, Date end) {
        return transactionRepository.findAllByCustomerIdAndCreateAtBetweenOrderByCreateAtDesc(customer_id, start, end);
    }

    public List<Transaction> findAllByCreateAtBetween(Date start, Date end) {
        return transactionRepository.findAllByCreateAtBetween(start, end);
    }

    public Optional<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction t) {
        return transactionRepository.save(t);
    }

    public Optional<Transaction> findBySongIdAndCustomerId(String song_id, String customer_id) {
        return transactionRepository.findBySongIdAndCustomerId(song_id, customer_id);
    }

//    DTO
    public TransactionDto findDtoById(String id) {
        return transactionRepository.findDtoById(id);
    }

    public List<TransactionDto> findAllDtoByTime(Date start, Date end) {
        return transactionRepository.findAllDtoByTime(start, end);
    }

    public List<TransactionDto> findAllDtoByCreatorAndTime(String creator_id, Date start, Date end) {
        return transactionRepository.findAllDtoByCreatorAndTime(creator_id, start, end);
    }

    public List<TransactionDto> findAllDtoBySongAndTime(String song_id, Date start, Date end) {
        return transactionRepository.findAllDtoBySongAndTime(song_id, start, end);
    }

    public List<TransactionDto> findAllDtoByCustomerAndTime(String customer_id, Date start, Date end) {
        return transactionRepository.findAllDtoByCustomerAndTime(customer_id, start, end);
    }
}

package com.example.finalpj.service;

import com.example.finalpj.entity.User;
import com.example.finalpj.reposiroty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User save(User u) {
        return userRepository.save(u);
    }

//    public List<User> findAll(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return userRepository.findAll(pageable).getContent();
//    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    public List<User> findTop20ByOrderByCreateAtDesc() {
//        return userRepository.findTop20ByOrderByCreateAtDesc();
//    }

//    public List<User> findTop20UserGetPaid() {
//        Pageable pageable = PageRequest.of(0, 20);
//        return userRepository.findTop20UserGetPaid(, pageable);
//    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}

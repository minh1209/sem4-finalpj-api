package com.example.finalpj.service;

import com.example.finalpj.dto.UserDto;
import com.example.finalpj.entity.User;
import com.example.finalpj.reposiroty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User save(User u) {
        return userRepository.save(u);
    }
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    //    DTO
    public List<UserDto> findAllDtoHaveTransactions(Date start, Date end) {
        return userRepository.findAllDtoHaveTransactions(start, end);
    }

    public List<UserDto> findAllDtoNotAdmin() {
        return userRepository.findAllDtoNotAdmin();
    }

    public List<UserDto> findAllDtoByUsernameSearch(String username) {
        return userRepository.findAllDtoByUsernameSearch(username);
    }

    public UserDto findDtoById(String id) {
        return userRepository.findDtoById(id);
    }

    public UserDto findDtoByEmail(String email) {
        return userRepository.findDtoByEmail(email);
    }

    public UserDto findDtoByUsername(String username) {
        return userRepository.findDtoByUsername(username);
    }
}

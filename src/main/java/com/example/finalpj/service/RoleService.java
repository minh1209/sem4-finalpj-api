package com.example.finalpj.service;

import com.example.finalpj.entity.Role;
import com.example.finalpj.reposiroty.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role save(Role role) {
        return roleRepository.save(role);
    }
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    public Optional<Role> findById(String id) {
        return roleRepository.findById(id);
    }
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

//    public List<Role> findAllByUserId(String id) {
//        return roleRepository.findAllByUserId(id);
//    }
//
//    @Transactional
//    public void deleteAllByUserId(String id) {
//        roleRepository.deleteAllByUserId(id);
//    }
}

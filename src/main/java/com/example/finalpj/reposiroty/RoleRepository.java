package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
//    List<Role> findAllByUserId(String id);
//    void deleteAllByUserId(String id);
}

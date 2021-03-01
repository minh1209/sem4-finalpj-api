package com.example.finalpj.reposiroty;

import com.example.finalpj.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

//    @Query(value = "select distinct u from User u join u.songs s where s.status = false order by size(s) desc")
//    List<User> findTop20UserGetPaid(Pageable pageable);

    @Query(value = "select u from User u join u.songs s group by u.id order by count(s.transactions) desc")
    List<User> findTop20UserGetPaid(Pageable pageable);

    @Query(value = "select u from User u where u.roles.size < 2")
    List<User> findAllUserNotAdmin();

    @Query(value = "select u from User u where lower(trim(u.username)) like %?1%")
    List<User> findAllByUsernameLike(String username);
//    @Query(value = "select u from User u join u.songs s order by ")
//    List<User> findAllCustom(Pageable pageable);
//    List<User> findTop20ByOrderByCreateAtDesc();
}

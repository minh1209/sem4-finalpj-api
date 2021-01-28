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

//    @Query(value = "select distinct u from User u join u.songs s where s.status = false order by size(s) desc")
//    List<User> findTop20UserGetPaid(Pageable pageable);

    @Query(value = "select u from User u join u.songs s group by u.id order by sum(s.paymentCount) desc")
    List<User> findTop20UserGetPaid(Pageable pageable);

//    @Query(value = "select u from User u join u.songs s order by ")
//    List<User> findAllCustom(Pageable pageable);
//    List<User> findTop20ByOrderByCreateAtDesc();
}

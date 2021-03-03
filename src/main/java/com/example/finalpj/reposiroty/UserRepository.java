package com.example.finalpj.reposiroty;

import com.example.finalpj.dto.UserDto;
import com.example.finalpj.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    //    DTO
    @Query(value = "select distinct u from User u inner join Song s on u.id = s.creator.id " +
            "inner join Transaction t on s.id = t.song.id where t is not null and t.createAt between ?1 and ?2")
    List<UserDto> findAllDtoHaveTransactions(Date start, Date end);

    @Query(value = "select u from User u where u.roles.size < 2")
    List<UserDto> findAllDtoNotAdmin();

    @Query(value = "select u from User u where lower(trim(u.username)) like %?1%")
    List<UserDto> findAllDtoByUsernameSearch(String username);

    @Query(value = "select u from User u where u.id = ?1")
    UserDto findDtoById(String id);

    @Query(value = "select u from User u where u.email = ?1")
    UserDto findDtoByEmail(String email);

    @Query(value = "select u from User u where u.username = ?1")
    UserDto findDtoByUsername(String username);
}

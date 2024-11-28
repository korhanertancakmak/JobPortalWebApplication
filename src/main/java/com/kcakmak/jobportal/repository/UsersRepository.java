package com.kcakmak.jobportal.repository;

import com.kcakmak.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    // Created to fix the duplicate registration bug that finds the users by a specific email
    Optional<Users> findByEmail(String email);
}

package com.cooksys.social_network_api.repositories;

import com.cooksys.social_network_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByDeletedFalse();

    Optional<User> findByCredentials_UsernameAndDeletedFalse(String username);

    Optional<User> findByCredentials_UsernameAndCredentials_PasswordAndDeletedTrue(String username, String password);

    Optional<User> findByCredentials_Username(String username);

    List<User> findAllByCredentials_UsernameInAndDeletedFalse(List<String> usernames);

    Optional<User> findByProfile_EmailAndDeletedFalse(String email);

}

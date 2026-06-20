package cl.innovatech.authService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.innovatech.authService.model.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserNameOrEmail(String userName, String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
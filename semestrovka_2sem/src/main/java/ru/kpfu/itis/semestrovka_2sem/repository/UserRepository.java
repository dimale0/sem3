package ru.kpfu.itis.semestrovka_2sem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.semestrovka_2sem.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Поиск по email (для аутентификации)
    Optional<User> findByEmail(String email);
    // Проверить, существует ли пользователь с таким email
    boolean existsByEmail(String email);
    // Проверить, существует ли пользователь с таким номером
    boolean existsByPhoneNumber(String phoneNumber);
}


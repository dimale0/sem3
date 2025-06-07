package ru.kpfu.itis.semestrovka_2sem.service;

import ru.kpfu.itis.semestrovka_2sem.dto.UserRegisterDto;
import ru.kpfu.itis.semestrovka_2sem.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(UserRegisterDto dto);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    User update(User user);

    void deleteById(Long id);
}


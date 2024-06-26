package com.suhwan.todolist.service;

import com.suhwan.todolist.domain.User;
import com.suhwan.todolist.dto.AddUserRequest;
import com.suhwan.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();

    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmailAndProvider(String email, String provider) {
        System.out.println("email = " + email + ", provider = " + provider);
        return userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}

package com.example.project_back.config;

import com.example.project_back.repository.UserRepository;

import java.text.Normalizer;
import java.util.Random;

public class UsernameGenerator {

    private static final Random RANDOM = new Random();

    public static String generateUsername(String fullName) {

        // Bỏ dấu tiếng Việt
        String username = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Đổi đ -> d
        username = username.replace("đ", "d")
                .replace("Đ", "D");

        // Chữ thường
        username = username.toLowerCase();

        // Chỉ giữ a-z và số
        username = username.replaceAll("[^a-z0-9 ]", "");

        // Bỏ khoảng trắng
        username = username.replaceAll("\\s+", "");

        return username;
    }

    public static String generateUniqueUsername(
            String fullName,
            UserRepository userRepository
    ) {

        String base = generateUsername(fullName);
        String username = base;

        while (userRepository.existsByUsername(username)) {
            username = base + RANDOM.nextInt(10000);
        }

        return username;
    }
}
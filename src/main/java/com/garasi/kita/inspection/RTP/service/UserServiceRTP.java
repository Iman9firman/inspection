package com.garasi.kita.inspection.RTP.service;

import com.garasi.kita.inspection.RTP.model.User;
import com.garasi.kita.inspection.RTP.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceRTP {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long userId, User userDetails) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            user.setName(userDetails.getName());
            user.setDetail(userDetails.getDetail());
            user.setRole(userDetails.getRole());
            user.setEnabled(true);
            return userRepository.save(user);
        });
    }
}

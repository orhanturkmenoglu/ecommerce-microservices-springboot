package com.example.auth_service.mapper;

import com.example.auth_service.dto.request.UserRegistrationRequestDTO;
import com.example.auth_service.enums.Role;
import com.example.auth_service.model.User;

public class UserMapper {

    public static User mapToUser(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        User user = new User();
        user.setEmail(userRegistrationRequestDTO.getEmail());
        user.setEmailVerified(false);
        user.setPassword(userRegistrationRequestDTO.getPassword());
        user.setRole(Role.ROLE_USER);
        return user;
    }
}

package org.example.userauthservice.mappers;

import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.models.Role;
import org.example.userauthservice.models.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .role(userDto.getRole()!=null?Role.valueOf(userDto.getRole()):Role.USER)
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
    }
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().toString());
        return userDto;
    }
}
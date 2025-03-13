package com.example.myapp.dto;

import com.example.myapp.model.User;
import lombok.Data;

@Data
public class UserDTO {
    Long id;
    String username;
    boolean isOfficial = false;
    boolean isSelf = false;

    public UserDTO(Long id, String username, boolean isOfficial, boolean isSelf) {
        this.id = id;
        this.username = username;
        this.isOfficial = isOfficial;
        this.isSelf = isSelf;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.isOfficial = user.isOfficial();
    }

    public UserDTO(User user, User loginUser) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.isOfficial = user.isOfficial();
        if (loginUser != null)
            this.isSelf = (user.getId().equals(loginUser.getId()));
    }

    public UserDTO() {}
}

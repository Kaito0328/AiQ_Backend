package com.example.myapp.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.service.UserService;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.dto.UserDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserDTO> getLoginUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // UserDetailsをCustomUserDetailsとして受け取り、Userを取得
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(new UserDTO(user));
    }

    @GetMapping("/official")
    public ResponseEntity<UserDTO> getOfficialUser() {
        return ResponseEntity.ok(new UserDTO(UserService.getOfficialUser()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<User> users = userService.findAll();
        User loginUser = (customUserDetails != null) ? customUserDetails.getUser() : null;
        List<UserDTO> userDTOs = userService.convertUserDTOs(users, loginUser);
        userDTOs = userDTOs.stream().filter(userDTO -> !userDTO.isOfficial() && !userDTO.isSelf())
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        User loginUser = (customUserDetails != null) ? customUserDetails.getUser() : null;
        UserDTO userDTO = new UserDTO(user, loginUser);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/id-only")
    public ResponseEntity<Long> getLoginUserId(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(user.getId());
    }

    @GetMapping("/id-only/official")
    public ResponseEntity<Long> getOfficialUserId() {
        return ResponseEntity.ok(UserService.getOfficialUser().getId());
    }
}

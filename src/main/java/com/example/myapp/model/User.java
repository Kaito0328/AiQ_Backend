package com.example.myapp.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import com.example.myapp.service.UserService;

@Entity
@Table(name = "user_table")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 必要に応じて、ユーザーに権限を付与する場合はこちらで返します。
        return null; // 今は権限を使わない場合は null を返します
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User() {}

    public boolean isOfficial() {
        return this.id.equals(UserService.getOfficialUserId());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // アカウントの有効期限が切れていない場合
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // アカウントがロックされていない場合
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 資格情報が有効である場合
    }

    @Override
    public boolean isEnabled() {
        return true; // アカウントが有効である場合
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

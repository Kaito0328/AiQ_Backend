package com.example.myapp.service;

import com.example.myapp.repository.UserRepository;
import com.example.myapp.dto.QuestionDTO;
import com.example.myapp.model.Question;
import com.example.myapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.myapp.dto.UserDTO;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static Long OFFICIAL_USER_ID = 1L;
    public static final String OFFICIAL_USER_NAME = "official_user";
    private static User official_user;

    public static Long getOfficialUserId() {
        return OFFICIAL_USER_ID;
    }

    public static User getOfficialUser() {
        return official_user;
    }

    public void saveOfficialUser() {
        Optional<User> optionalOfficialUser = userRepository.findByUsername(OFFICIAL_USER_NAME);

        if (optionalOfficialUser.isEmpty()) {
            User officialUser = new User();
            officialUser.setUsername(OFFICIAL_USER_NAME);
            officialUser.setPassword(null); // パスワードはnull
            officialUser = userRepository.save(officialUser); // ID は自動生成される

            // official_user を代入
            official_user = officialUser;
        } else {
            // 既存の公式ユーザーをセット
            official_user = optionalOfficialUser.get();
        }

        // OFFICIAL_USER_ID を更新
        OFFICIAL_USER_ID = official_user.getId();
    }


    public List<UserDTO> convertUserDTOs(List<User> users) {
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public List<UserDTO> convertUserDTOs(List<User> users, User loginUser) {
        return users.stream().map(user -> new UserDTO(user, loginUser)) // ログインユーザーを使って、isSelfフラグを設定
                .collect(Collectors.toList());
    }

    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("ユーザー名が既に存在します");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("パスワードが正しくありません");
        }
        return user;
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));
        return user; // UserクラスがUserDetailsを実装しているのでそのまま返す
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ユーザーをリポジトリから取得
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 認証用に UserDetails を返す
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword()).roles("USER") // 必要に応じて役割を設定
                .build();
    }
}

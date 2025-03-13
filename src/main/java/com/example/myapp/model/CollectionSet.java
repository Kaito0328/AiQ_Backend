package com.example.myapp.model;

import jakarta.persistence.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@Entity
@Table(name = "collection_sets")
public class CollectionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // コレクションセットを作成したユーザー

    private String name; // コレクションセット名 (ユーザー名や分野など)
    private boolean isPublic; // 公開か非公開かを設定

    @OneToMany(mappedBy = "collectionSet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Collection> collections; // このコレクションセットに紐づくコレクション群

    public CollectionSet() {}

    public CollectionSet(User user, String name, boolean isPublic) {
        this.user = user;
        this.name = name;
        this.isPublic = isPublic;
    }

    // ゲッター・セッター
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

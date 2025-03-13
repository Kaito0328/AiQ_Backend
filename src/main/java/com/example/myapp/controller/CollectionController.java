package com.example.myapp.controller;

import java.util.List;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.service.CollectionService;
import com.example.myapp.service.CollectionSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
import com.example.myapp.dto.CollectionDTO;
import com.example.myapp.dto.CollectionSetDTO;
import com.example.myapp.model.Collection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.myapp.service.UserService;


@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private final CollectionService collectionService;
    private final CollectionSetService collectionSetService;
    private final UserService userService;

    @Autowired
    public CollectionController(CollectionService collectionService,
            CollectionSetService collectionSetService, UserService userService) {
        this.collectionService = collectionService;
        this.collectionSetService = collectionSetService;
        this.userService = userService;
    }

    // @GetMapping("/user")
    // public ResponseEntity<List<CollectionDTO>> getUserCollections(
    // @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    // User user = customUserDetails.getUser();
    // List<Collection> collections = collectionSetService.getCollectionsByUser(user, user);
    // List<CollectionDTO> collectionDTOs = collectionService.convertCollectionDTOs(collections);
    // return ResponseEntity.ok(collectionDTOs);
    // }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CollectionDTO>> getCollectionSetsByUserId(@PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User owner = userService.getUserById(userId);
        if (owner == null) {
            return ResponseEntity.notFound().build();
        }

        User viewer = (customUserDetails != null) ? customUserDetails.getUser() : null;

        List<Collection> collections = collectionSetService.getCollectionsByUser(viewer, owner);
        List<CollectionDTO> collectionDTOs = collectionService.convertCollectionDTOs(collections);
        return ResponseEntity.ok(collectionDTOs);
    }

    @GetMapping("/collection-sets/{collectionSetId}")
    public ResponseEntity<List<CollectionDTO>> getCollectionsByCollectionSetId(
            @PathVariable Long collectionSetId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User viewer = (customUserDetails != null) ? customUserDetails.getUser() : null;
        List<Collection> collections =
                collectionSetService.getCollectionsByCollectionSetId(collectionSetId, viewer);
        List<CollectionDTO> collectionDTOs = collectionService.convertCollectionDTOs(collections);
        return ResponseEntity.ok(collectionDTOs);
    }
}

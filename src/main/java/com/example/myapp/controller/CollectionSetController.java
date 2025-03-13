package com.example.myapp.controller;

import java.util.List;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.service.CollectionService;
import com.example.myapp.service.CollectionSetService;
import com.example.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.persistence.*;
import com.example.myapp.model.Collection;

import com.example.myapp.dto.CollectionSetDTO;


@RestController
@RequestMapping("/api/collection-sets")
public class CollectionSetController {

        private final CollectionSetService collectionSetService;
        private final UserService userService;

        @Autowired
        public CollectionSetController(CollectionSetService collectionSetService,
                        UserService userService) {
                this.collectionSetService = collectionSetService;
                this.userService = userService;
        }

        // @GetMapping("/user")
        // public ResponseEntity<List<CollectionSetDTO>> getUserCollectionSets(
        // @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // User user = customUserDetails.getUser();
        // List<CollectionSet> collectionSets =
        // collectionSetService.getCollectionSetsByUser(user, user);
        // List<CollectionSetDTO> collectionSetDTOs =
        // collectionSetService.convertCollectionDTOs(collectionSets);
        // return ResponseEntity.ok(collectionSetDTOs);
        // }

        @GetMapping("/user/{userId}")
        public ResponseEntity<List<CollectionSetDTO>> getCollectionSetsByUserId(
                        @PathVariable Long userId,
                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

                User owner = userService.getUserById(userId);
                if (owner == null) {
                        return ResponseEntity.notFound().build();
                }

                User viewer = (customUserDetails != null) ? customUserDetails.getUser() : null;

                List<CollectionSet> collectionSets =
                                collectionSetService.getCollectionSetsByUser(viewer, owner);
                List<CollectionSetDTO> collectionSetDTOs =
                                collectionSetService.convertCollectionDTOs(collectionSets);
                return ResponseEntity.ok(collectionSetDTOs);
        }

        // @GetMapping("/official")
        // public ResponseEntity<List<CollectionSetDTO>> getOfficialCollectionSets() {
        // User officialUser = UserService.getOfficialUser();
        // List<CollectionSet> collectionSets =
        // collectionSetService.getCollectionSetsByUser(null, officialUser);
        // List<CollectionSetDTO> collectionSetDTOs =
        // collectionSetService.convertCollectionDTOs(collectionSets);
        // return ResponseEntity.ok(collectionSetDTOs);
        // }


        // @PostMapping("/create")
        // public ResponseEntity<CollectionSet> createCollectionSet(
        // @AuthenticationPrincipal CustomUserDetails customUserDetails,
        // @RequestParam String name) {
        // User user = customUserDetails.getUser();
        // CollectionSet collectionSet = collectionSetService.createCollectionSet(user, name);
        // return ResponseEntity.ok(collectionSet);
        // }

        // @PostMapping("/addCollection")
        // public ResponseEntity<Collection> addCollectionToSet(
        // @AuthenticationPrincipal CustomUserDetails customUserDetails,
        // @RequestParam String collectionSetName, @RequestParam String collectionName) {

        // User user = customUserDetails.getUser();
        // Collection collection =
        // collectionService.getOrCreateCollection(user, collectionSetName, collectionName);
        // return ResponseEntity.ok(collection);
        // }
}

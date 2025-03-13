package com.example.myapp.service;

import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.User;
import com.example.myapp.repository.CollectionRepository;
import com.example.myapp.repository.CollectionSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.myapp.model.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.myapp.dto.CollectionDTO;
import com.example.myapp.dto.CollectionSetDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;


@Service
public class CollectionSetService {
    private final CollectionSetRepository collectionSetRepository;

    @Autowired
    public CollectionSetService(CollectionSetRepository collectionSetRepository) {
        this.collectionSetRepository = collectionSetRepository;
    }

    public List<CollectionSetDTO> convertCollectionDTOs(List<CollectionSet> collections) {
        return collections.stream().map(CollectionSetDTO::new).collect(Collectors.toList());
    }

    public boolean accessable(User viewer, CollectionSet collectionSet) {
        if (collectionSet.isPublic())
            return true;

        if (viewer != null) {
            if (viewer.getId().equals(collectionSet.getUser().getId()))
                return true;
            if (viewer.isOfficial())
                return true;
        }

        return false;
    }

    public List<CollectionSet> filterCollectionSets(User viewer,
            List<CollectionSet> collectionSets) {
        return collectionSets.stream().filter(collectionSet -> {
            return accessable(viewer, collectionSet);
        }).collect(Collectors.toList());
    }

    public Optional<CollectionSet> findByUserAndName(User user, String name) {
        return collectionSetRepository.findByUserAndName(user, name);
    }

    public CollectionSet createCollectionSet(User user, String name, boolean isPublic) {
        return collectionSetRepository.findByUserAndName(user, name).orElseGet(
                () -> collectionSetRepository.save(new CollectionSet(user, name, isPublic)));
    }

    public List<CollectionSet> getCollectionSetsByUser(User viewer, User owner) {
        // コレクションセットのオーナーと閲覧者を引数に取り、判定を行う
        List<CollectionSet> collectionSets = collectionSetRepository.findAllByUser(owner);
        return filterCollectionSets(viewer, collectionSets);
    }

    public List<Collection> getCollectionsByUser(User viewer, User owner) {
        List<CollectionSet> collectionSets = getCollectionSetsByUser(viewer, owner);

        return collectionSets.stream()
                .flatMap(collectionSet -> collectionSet.getCollections().stream())
                .collect(Collectors.toList());
    }

    public CollectionSet getCollectionSetById(Long Id, User viewer) {
        // コレクションセットを取得
        CollectionSet collectionSet = collectionSetRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("CollectionSet not found"));

        // コレクションセットが非公開であれば、ユーザーが所有者であるか、公式ユーザーである場合にのみアクセス
        if (!accessable(viewer, collectionSet)) {
            throw new AccessDeniedException(
                    "You do not have permission to access this collection set");
        }

        return collectionSet;
    }

    public List<Collection> getCollectionsByCollectionSetId(Long collectionSetId, User viewer) {
        CollectionSet collectionSet = getCollectionSetById(collectionSetId, viewer);

        return collectionSet.getCollections();
    }


    // public List<CollectionSetResponse> getCollectionSets() {
    // List<CollectionSet> sets = collectionSetRepository.findAll();

    // return sets.stream().map(set -> {
    // List<Long> sortedCollectionIds =
    // collectionRepository.findByCollectionSetIdOrderByNameAsc(set.getId()).stream()
    // .map(Collection::getId).collect(Collectors.toList());
    // return new CollectionSetResponse(set.getId(), set.getName(), sortedCollectionIds);
    // }).collect(Collectors.toList());
    // }
}

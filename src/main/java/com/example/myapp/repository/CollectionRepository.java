package com.example.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.myapp.model.Collection;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByCollectionSetIdOrderByNameAsc(Long collectionSetId);

    Optional<Collection> findByCollectionSetAndName(CollectionSet collectionSet, String name);

    List<Collection> findAllByCollectionSet(CollectionSet collectionSet);

    List<Collection> findAllByCollectionSetId(Long collectionSetId);
}


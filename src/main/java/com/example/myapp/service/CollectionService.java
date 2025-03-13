package com.example.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.myapp.dto.CollectionDTO;
import com.example.myapp.dto.ProblemGenerationRequest;
import com.example.myapp.dto.QuestionDTO;
import com.example.myapp.model.Collection;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.User;
import com.example.myapp.repository.CollectionRepository;

@Service
public class CollectionService {

  private final CollectionRepository collectionRepository;

  @Autowired
  public CollectionService(CollectionRepository collectionRepository) {
    this.collectionRepository = collectionRepository;
  }

  public List<CollectionDTO> convertCollectionDTOs(List<Collection> collections) {
    return collections.stream().map(CollectionDTO::new).collect(Collectors.toList());
  }

  public Collection createCollection(CollectionSet collectionSet, String collectionName,
      boolean generatedByAi) {
    return collectionRepository.findByCollectionSetAndName(collectionSet, collectionName)
        .orElseGet(() -> collectionRepository
            .save(new Collection(collectionName, collectionSet, generatedByAi)));
  }



  // // コレクションを全て取得
  // public List<CollectionResponse> getCollections() {
  // List<Collection> collections = collectionRepository.findAll();

  // return collections.stream()
  // .map(collection -> new CollectionResponse(collection.getId(), collection.getName(),
  // collection.getCollectionSet().getId())) // CollectionSet を経由して User にアクセス
  // .collect(Collectors.toList());
  // }


  // public Collection createCollection(String collectionName, User user, String collectionSetName)
  // {
  // CollectionSet collectionSet =
  // collectionSetService.getOrCreateCollectionSet(user, collectionSetName);

  // Optional<Collection> existingCollection =
  // collectionRepository.findByCollectionSetAndName(collectionSet, collectionName);
  // if (existingCollection.isPresent()) {
  // return existingCollection.get();
  // }

  // Collection collection = new Collection(collectionName, collectionSet);
  // return collectionRepository.save(collection);
  // }


  // public List<Collection> findByUserAndSetName(User user, String collectionSetName) {
  // CollectionSet collectionSet =
  // collectionSetService.getOrCreateCollectionSet(user, collectionSetName);
  // return collectionRepository.findAllByCollectionSet(collectionSet);
  // }
}

package com.example.myapp.dto;

import com.example.myapp.model.CollectionSet;

public class CollectionSetDTO {
    private Long id;
    private String name;

    public CollectionSetDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CollectionSetDTO(CollectionSet collectionSet) {
        this.id = collectionSet.getId();
        this.name = collectionSet.getName();
    }

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
}

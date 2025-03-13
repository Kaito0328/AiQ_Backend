package com.example.myapp.dto;

import com.example.myapp.model.Collection;

public class CollectionDTO {
    private Long id;
    private String name;

    public CollectionDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CollectionDTO(Collection collection) {
        this.id = collection.getId();
        this.name = collection.getName();
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

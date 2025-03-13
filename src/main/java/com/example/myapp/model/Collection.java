package com.example.myapp.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.myapp.model.CollectionSet;

@Entity
@Getter
@Setter
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "collection_set_id", nullable = false)
    private CollectionSet collectionSet;

    @Column(nullable = false)
    private boolean generatedByAi;

    public boolean isGeneratedByAi() {
        return generatedByAi;
    }

    public void setGeneratedByAi(boolean generatedByAi) {
        this.generatedByAi = generatedByAi;
    }

    public Collection() {}

    public Collection(String name, CollectionSet collectionSet, boolean generatedByAi) {
        this.name = name;
        this.collectionSet = collectionSet;
        this.generatedByAi = generatedByAi;
    }
}

package com.languageapp.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    private Long id;

    public Entity() {
    }

    public Entity(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

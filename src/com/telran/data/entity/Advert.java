package com.telran.data.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Advert {
    private UUID id;
    private String owner;
    private LocalDateTime dateTime;
    private String content;



    public Advert(UUID id, String owner, LocalDateTime dateTime, String content) {
        this.id = id;
        this.owner = owner;
        this.content = content;
        this.dateTime = dateTime;
    }

    public UUID id() {
        return id;
    }

    public String owner() {
        return owner;
    }

    public String content() {
        return content;
    }

    public LocalDateTime date() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advert advert = (Advert) o;
        return Objects.equals(id, advert.id);
    }

    @Override
    public String toString() {
        return  "id=" + id + "&owner=" + owner + "&content='" + content + "&dateTime=" + dateTime;
    }
}

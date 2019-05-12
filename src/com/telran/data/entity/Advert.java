package com.telran.data.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Advert {
    private UUID id;
    private String owner;
    private String content;
    private LocalDateTime dateTime;


    public Advert(UUID id, String owner, String content, LocalDateTime dateTime) {
        this.id = id;
        this.owner = owner;
        this.content = content;
        this.dateTime = dateTime;



    }

    public UUID getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateTime() {
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
        return "Advert{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", content='" + content + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}

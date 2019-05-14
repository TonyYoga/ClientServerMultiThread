package com.telran.dto;

import java.util.UUID;

public class AddAdvertResponseDto {
    UUID id;

    public AddAdvertResponseDto() {
    }

    public AddAdvertResponseDto(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}

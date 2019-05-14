package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * dto data trancfer object Advert
 */
public class AdvertDto {
    UUID id;
    String owner;
    LocalDateTime dateTime;
    String content;

    public AdvertDto() {
    }

    public AdvertDto(UUID id, String owner, LocalDateTime dateTime, String content) {
        this.id = id;
        this.owner = owner;
        this.dateTime = dateTime;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                "&owner=" + owner +
                "&dateTime=" + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                "&content=" + content;
    }
}

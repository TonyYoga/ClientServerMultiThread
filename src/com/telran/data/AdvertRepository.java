package com.telran.data;

import com.telran.data.entity.Advert;

import java.time.LocalDate;
import java.util.UUID;

public interface AdvertRepository {
    boolean addAdvert(Advert advert);

    Advert remove(UUID uuid);

    Iterable<Advert> find(String owner);

    Iterable<Advert> find(LocalDate date);

    Iterable<Advert> find(LocalDate start, LocalDate end);
}

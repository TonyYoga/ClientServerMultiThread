package com.telran.data;

import com.telran.data.entity.Advert;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AdvertRepositoryImpl implements AdvertRepository {
    private final ConcurrentHashMap<UUID, Advert> advertIdMap;
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Advert>> ownerNameMap;
    private final ConcurrentSkipListMap<LocalDate, CopyOnWriteArrayList<Advert>> dateMap;

    public AdvertRepositoryImpl() {
        advertIdMap = new ConcurrentHashMap<>();
        ownerNameMap = new ConcurrentHashMap<>();
        dateMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public UUID addAdvert(Advert advert) {
        Objects.requireNonNull(advert);
        for (; ; ) {
            if (advertIdMap.putIfAbsent(advert.getId(), advert) != null) {
                return null;
            }
            ownerNameMap.computeIfAbsent(advert.getOwner(), advertList -> new CopyOnWriteArrayList<>()).addIfAbsent(advert);
            dateMap.computeIfAbsent(advert.getDateTime().toLocalDate(), advertList -> new CopyOnWriteArrayList<>()).addIfAbsent(advert);
            if (advertIdMap.containsKey(advert.getId())) {
                return advert.getId();
            }
        }

    }

    @Override
    public Advert remove(UUID uuid) {
        Objects.requireNonNull(uuid);
        for (; ; ) {
            Advert curr = advertIdMap.remove(uuid);
            if (curr != null) {
                CopyOnWriteArrayList<Advert> ownerList =  ownerNameMap.get(curr.getOwner());
                CopyOnWriteArrayList<Advert> dateList = dateMap.get(curr.getDateTime().toLocalDate());
                if (ownerList != null) {
                    ownerList.remove(curr);
                }
                if (dateList != null) {
                    dateList.remove(curr);
                }

            }
            if (!advertIdMap.containsKey(uuid)) {
                return curr;
            }
        }
    }

    @Override
    public Iterable<Advert> find(String owner) {
        return ownerNameMap.get(owner).stream()
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Iterable<Advert> find(LocalDate date) {
        return dateMap.get(date).stream()
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Iterable<Advert> find(LocalDate start, LocalDate end) {
        return dateMap.subMap(start, true, end, true).values().stream()
                .flatMap(list -> list.stream())
                .collect(Collectors.toUnmodifiableList());
    }
}

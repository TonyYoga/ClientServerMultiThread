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
    public boolean addAdvert(Advert advert) {
        Objects.requireNonNull(advert);
        for (; ; ) {
            if (advertIdMap.putIfAbsent(advert.id(), advert) != null) {
                return false;
            }
            ownerNameMap.computeIfAbsent(advert.owner(), advertList -> new CopyOnWriteArrayList<>()).addIfAbsent(advert);
            dateMap.computeIfAbsent(advert.date().toLocalDate(), advertList -> new CopyOnWriteArrayList<>()).addIfAbsent(advert);
            if (!advertIdMap.containsKey(advert.id())) {
                continue;
            }
            break;
        }
        return true;
    }

    @Override
    public Advert remove(UUID uuid) {
        for (; ; ) {
            Advert curr = advertIdMap.remove(uuid);
            if (curr != null) {
                CopyOnWriteArrayList<Advert> ownerList =  ownerNameMap.get(curr.owner());
                CopyOnWriteArrayList<Advert> dateList = dateMap.get(curr.date().toLocalDate());
                if (ownerList != null) {
                    ownerList.remove(curr);
                }
                if (dateList != null) {
                    dateList.remove(curr);
                }
                if (advertIdMap.containsKey(uuid)) {
                    continue;
                }
            }
            return curr;

        }
    }

    @Override
    public Iterable<Advert> find(String owner) {
        return ownerNameMap.get(owner);
    }

    @Override
    public Iterable<Advert> find(LocalDate date) {
        return dateMap.get(date);
    }

    @Override
    public Iterable<Advert> find(LocalDate start, LocalDate end) {
        return dateMap.subMap(start, true, end, true)
                .values()
                .stream()
                .flatMap(list -> list.stream())
                .collect(Collectors.toUnmodifiableList());
    }
}

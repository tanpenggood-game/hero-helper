package com.itplh.hero.service.impl;

import com.itplh.hero.domain.HeroRegionUser;
import com.itplh.hero.service.HeroRegionUserService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class HeroRegionUserServiceImpl implements HeroRegionUserService {

    /**
     * key sid
     * value {@link HeroRegionUser}
     */
    private static final ConcurrentSkipListMap<String, HeroRegionUser> regionUserContainer = new ConcurrentSkipListMap<>();

    @Override
    public boolean save(HeroRegionUser heroRegionUser) {
        if (Objects.isNull(heroRegionUser) || Objects.isNull(heroRegionUser.getSid())) {
            return false;
        }
        regionUserContainer.put(heroRegionUser.getSid(), heroRegionUser);
        return true;
    }

    @Override
    public boolean delete(String sid) {
        if (Objects.isNull(sid)) {
            return false;
        }
        return Optional.ofNullable(regionUserContainer.remove(sid)).isPresent();
    }

    @Override
    public int deleteAll() {
        int[] removeCounter = {0};
        regionUserContainer.keySet()
                .forEach(sid -> {
                    if (delete(sid)) {
                        ++removeCounter[0];
                    }
                });
        return removeCounter[0];
    }

    @Override
    public Optional<HeroRegionUser> get(String sid) {
        if (Objects.isNull(sid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(regionUserContainer.get(sid));
    }

    @Override
    public Collection<HeroRegionUser> getAll() {
        return regionUserContainer.values();
    }

    @Override
    public Optional<HeroRegionUser> getFirst() {
        return regionUserContainer.values().stream().findFirst();
    }

    @Override
    public boolean contains(String sid) {
        return get(sid).isPresent();
    }

}

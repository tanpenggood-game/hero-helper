package com.itplh.hero.context;


import com.itplh.hero.domain.HeroRegionUser;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeroRegionUserContext {

    /**
     * key sid
     * value {@link HeroRegionUser}
     */
    private static final ConcurrentSkipListMap<String, HeroRegionUser> regionUserContainer = new ConcurrentSkipListMap<>();

    public static boolean save(HeroRegionUser heroRegionUser) {
        if (Objects.isNull(heroRegionUser) || Objects.isNull(heroRegionUser.getSid())) {
            return false;
        }
        regionUserContainer.put(heroRegionUser.getSid(), heroRegionUser);
        return true;
    }

    public static boolean delete(String sid) {
        if (Objects.isNull(sid)) {
            return false;
        }
        return Optional.ofNullable(regionUserContainer.remove(sid)).isPresent();
    }

    public static boolean deleteAll() {
        int total = regionUserContainer.size();
        int[] removeCounter = {0};
        regionUserContainer.keySet()
                .forEach(sid -> {
                    boolean isSuccess = delete(sid);
                    if (isSuccess) {
                        ++removeCounter[0];
                    }
                });
        return total == removeCounter[0];
    }

    public static Optional<HeroRegionUser> get(String sid) {
        if (Objects.isNull(sid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(regionUserContainer.get(sid));
    }

    public static Collection<HeroRegionUser> getAll() {
        return regionUserContainer.values();
    }

    public static Optional<HeroRegionUser> getFirst() {
        return regionUserContainer.values().stream().findFirst();
    }

    public static boolean contains(String sid) {
        return get(sid).isPresent();
    }

}

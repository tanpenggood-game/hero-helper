package com.itplh.hero.service;

import com.itplh.hero.domain.HeroRegionUser;

import java.util.Collection;
import java.util.Optional;

public interface HeroRegionUserService {

    boolean save(HeroRegionUser heroRegionUser);

    boolean delete(String sid);

    /**
     * @return success remove counter
     */
    int deleteAll();

    Optional<HeroRegionUser> get(String sid);

    Collection<HeroRegionUser> getAll();

    boolean contains(String sid);

}

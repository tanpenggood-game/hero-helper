package com.itplh.hero.service.impl;

import com.itplh.hero.domain.HeroRegionUser;
import com.itplh.hero.mapper.RegionUserMapper;
import com.itplh.hero.service.HeroRegionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class HeroRegionUserServiceImpl implements HeroRegionUserService {

    @Autowired
    private RegionUserMapper regionUserMapper;

    @Override
    public boolean save(HeroRegionUser heroRegionUser) {
        if (Objects.isNull(heroRegionUser) || Objects.isNull(heroRegionUser.getSid())) {
            return false;
        }
        HeroRegionUser dbUser = regionUserMapper.selectBySid(heroRegionUser.getSid());
        int affectedRows;
        if (Objects.isNull(dbUser)) {
            heroRegionUser.setCreateTime(LocalDateTime.now());
            affectedRows = regionUserMapper.insert(heroRegionUser);
        } else {
            heroRegionUser.setId(dbUser.getId());
            heroRegionUser.setLastUpdateTime(LocalDateTime.now());
            affectedRows = regionUserMapper.updateById(heroRegionUser);
        }
        return affectedRows > 0;
    }

    @Override
    public boolean delete(String sid) {
        if (Objects.isNull(sid)) {
            return false;
        }
        int affectedRows = regionUserMapper.deleteBySid(sid);
        return affectedRows > 0;
    }

    @Override
    public int deleteAll() {
        int[] removeCounter = {0};
        getAll().stream()
                .map(HeroRegionUser::getId)
                .forEach(id -> removeCounter[0] += regionUserMapper.deleteById(id));
        return removeCounter[0];
    }

    @Override
    public Optional<HeroRegionUser> get(String sid) {
        if (Objects.isNull(sid)) {
            return Optional.empty();
        }
        return Optional.ofNullable(regionUserMapper.selectBySid(sid));
    }

    @Override
    public Collection<HeroRegionUser> getAll() {
        return regionUserMapper.selectList(null);
    }

    @Override
    public boolean contains(String sid) {
        return get(sid).isPresent();
    }

}

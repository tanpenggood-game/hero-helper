package com.itplh.hero.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itplh.hero.domain.HeroRegionUser;

public interface RegionUserMapper extends BaseMapper<HeroRegionUser> {

    HeroRegionUser selectBySid(String sid);

    int deleteBySid(String sid);

}

package com.itplh.hero.properties;


import com.itplh.hero.domain.HeroRegionUser;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "hero")
public class HeroRegionUserProperties {

    /**
     * key sid
     * value {@link HeroRegionUser}
     */
    private Map<String, HeroRegionUser> regionUsers;

}

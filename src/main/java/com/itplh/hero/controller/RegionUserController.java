package com.itplh.hero.controller;

import com.itplh.hero.context.HeroRegionUserContext;
import com.itplh.hero.domain.HeroRegionUser;
import com.itplh.hero.listener.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class RegionUserController {

    @Autowired
    private EventBus eventBus;

    @PostMapping("/save")
    public Result save(@RequestBody HeroRegionUser regionUser) {
        if (Objects.isNull(regionUser) || StringUtils.isEmpty(regionUser.getSid())) {
            return Result.error("表单数据异常");
        }
        return Result.ok(HeroRegionUserContext.save(regionUser));
    }

    @PostMapping("/delete")
    public Result delete(String sid) {
        if (StringUtils.isEmpty(sid)) {
            return Result.error("sid is required.");
        }
        // close event
        eventBus.close(sid);
        // delete user
        boolean isSuccess = HeroRegionUserContext.delete(sid);
        return Result.ok("[isSuccess=]" + isSuccess);
    }

    @PostMapping("/delete-all")
    public Result deleteAll() {
        // close all event
        HeroRegionUserContext.getAll().forEach(user -> eventBus.close(user.getSid()));
        // delete all user
        return Result.ok(HeroRegionUserContext.deleteAll());
    }

    @GetMapping("/get-all")
    public Result<Collection<HeroRegionUser>> getAll() {
        return Result.ok(HeroRegionUserContext.getAll());
    }

}

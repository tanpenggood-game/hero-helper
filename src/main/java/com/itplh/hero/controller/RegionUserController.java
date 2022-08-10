package com.itplh.hero.controller;

import com.itplh.hero.domain.HeroRegionUser;
import com.itplh.hero.listener.EventBus;
import com.itplh.hero.service.HeroRegionUserService;
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

    @Autowired
    private HeroRegionUserService heroRegionUserService;

    @PostMapping("/save")
    public Result save(@RequestBody HeroRegionUser regionUser) {
        if (Objects.isNull(regionUser)
                || StringUtils.isEmpty(regionUser.getSid())
                || StringUtils.isEmpty(regionUser.getDomain())
                || StringUtils.isEmpty(regionUser.getPort())
        ) {
            return Result.error("表单数据异常");
        }
        return Result.ok(heroRegionUserService.save(regionUser));
    }

    @PostMapping("/delete")
    public Result delete(String sid) {
        if (StringUtils.isEmpty(sid)) {
            return Result.error("sid is required.");
        }
        if (eventBus.containsEvent(sid)) {
            return Result.error("please close related event.");
        }
        return Result.ok(String.format("[delete user={}] [close event={}]",
                heroRegionUserService.delete(sid),
                eventBus.close(sid)));
    }

    @PostMapping("/delete-all")
    public Result deleteAll() {
        eventBus.getAllEvent().stream().map(e -> e.eventContext().getUser().getSid())
                .forEach(eventBus::close);
        return Result.ok(String.format("[delete user={}] [close event={}]",
                heroRegionUserService.deleteAll(),
                eventBus.closeAll()));
    }

    @GetMapping("/get-all")
    public Result<Collection<HeroRegionUser>> getAll() {
        return Result.ok(heroRegionUserService.getAll());
    }

}

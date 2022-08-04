package com.itplh.hero.controller;

import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.listener.EventBus;
import com.itplh.hero.properties.HeroRegionUserProperties;
import com.itplh.hero.util.EventTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private HeroRegionUserProperties heroRegionUserProperties;

    @PostMapping("/trigger")
    public Result eventTrigger(@RequestParam(required = true) String eventName,
                               @RequestParam(required = false) String sid,
                               @RequestParam(required = false, defaultValue = "1") Long targetRunRound,
                               @RequestBody Map<String, String> extendInfo) {
        Result validate = validate(eventName, sid, extendInfo);
        if (!validate.isSuccess()) {
            return validate;
        }
        sid = setDefaultSidIfAbsent(sid);

        log.info("start trigger [sid={}] [eventName={}]", sid, eventName);
        HeroEventContext heroEventContext = HeroEventContext.newInstance(sid, eventName, targetRunRound, extendInfo);
        boolean isSuccess = EventTemplateUtil.getEventInstance(eventName, heroEventContext)
                .map(event -> eventBus.publishEvent(event))
                .orElse(false);
        log.info("finish trigger [sid={}] [eventName={}] [isSuccess={}]", sid, eventName, isSuccess);
        return Result.ok(String.format("finish trigger [sid=%s] [eventName=%s]  [isSuccess=%s]", sid, eventName, isSuccess));
    }

    @PostMapping("/close")
    public Result eventClose(String sid) {
        sid = setDefaultSidIfAbsent(sid);

        log.info("start close [sid={}]", sid);
        boolean isSuccess = eventBus.close(sid);
        log.info("finish close [sid={}] [isSuccess={}]", sid, isSuccess);
        return Result.ok(String.format("[sid=%s] [isSuccess=%s]", sid, isSuccess));
    }

    @PostMapping("/close-all")
    public Result eventCloseAll() {
        log.info("start close all");
        int closeCounter = operateAll("closed", eventBus::close);
        log.info("finish close all [total closed={}]", closeCounter);
        return Result.ok(String.format("finish close all [total closed=%s]", closeCounter));
    }

    @PostMapping("/pause")
    public Result eventPause(String sid) {
        sid = setDefaultSidIfAbsent(sid);

        log.info("start pause [sid={}]", sid);
        boolean isSuccess = eventBus.pause(sid);
        log.info("finish pause [sid={}] [isSuccess={}]", sid, isSuccess);
        return Result.ok(String.format("[sid=%s] [isSuccess=%s]", sid, isSuccess));
    }

    @PostMapping("/pause-all")
    public Result eventPauseAll() {
        log.info("start pause all");
        int pauseCounter = operateAll("paused", eventBus::pause);
        log.info("finish pause all [total pause={}]", pauseCounter);
        return Result.ok(String.format("finish pause all [total paused=%s]", pauseCounter));
    }

    @PostMapping("/restart")
    public Result eventContinue(String sid) {
        sid = setDefaultSidIfAbsent(sid);

        log.info("start restart [sid={}]", sid);
        boolean isSuccess = eventBus.restart(sid);
        log.info("finish restart [sid={}] [isSuccess={}]", sid, isSuccess);
        return Result.ok(String.format("[sid=%s] [isSuccess=%s]", sid, isSuccess));
    }

    @PostMapping("/restart-all")
    public Result eventContinueAll() {
        log.info("start restart all");
        int restartCounter = operateAll("restarted", eventBus::restart);
        log.info("finish restart all [total restarted={}]", restartCounter);
        return Result.ok(String.format("finish restart all [total restarted=%s]", restartCounter));
    }

    @GetMapping("/get")
    public Result eventOne(String sid) {
        sid = setDefaultSidIfAbsent(sid);
        return Result.ok(eventBus.getEvent(sid));
    }

    @GetMapping("/get-all")
    public Result eventAll() {
        return Result.ok(eventBus.getAllEvent());
    }

    private String setDefaultSidIfAbsent(String sid) {
        if (StringUtils.isEmpty(sid)) {
            String firstSid = Optional.ofNullable(heroRegionUserProperties.getRegionUsers())
                    .flatMap(roles -> roles.keySet().stream().findFirst())
                    .orElse(null);
            if (StringUtils.isEmpty(firstSid)) {
                throw new RuntimeException("Don't found suitable sid");
            }
            return firstSid;
        }
        return sid;
    }

    private Result validate(String eventName,
                            String sid,
                            Map<String, String> extendInfo) {
        if (StringUtils.hasText(sid)) {
            if (!heroRegionUserProperties.getRegionUsers().keySet().contains(sid)) {
                return Result.error("sid 参数异常");
            }
        }
        if (StringUtils.isEmpty(eventName)) {
            return Result.error("eventName 参数为空");
        }
        if (!EventTemplateUtil.hasEventInstance(eventName)) {
            return Result.error("eventName 参数异常");
        }
        if (Objects.equals(eventName, NPCFixedEvent.class.getSimpleName())) {
            String resource = ParameterEnum.RESOURCE.getName();
            if (CollectionUtils.isEmpty(extendInfo) || !extendInfo.containsKey(resource)) {
                return Result.error(String.format("%s must set %s", NPCFixedEvent.class.getSimpleName(), resource));
            }
        }
        return Result.ok();
    }

    private int operateAll(String operateLog, Function<String, Boolean> function) {
        int[] successCounter = {0};
        eventBus.getAllEvent().stream()
                .map(event -> event.eventContext().getSid())
                .forEach(sid -> {
                    boolean isSuccess = function.apply(sid);
                    if (isSuccess) {
                        ++successCounter[0];
                        log.info(operateLog + " [sid={}]", sid);
                    }
                });
        return successCounter[0];
    }

}



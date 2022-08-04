package com.itplh.hero.controller;

import com.itplh.hero.domain.Option;
import com.itplh.hero.domain.OptionGroup;
import com.itplh.hero.util.EventTemplateUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @GetMapping("/get")
    public Result<List<OptionGroup>> resourceGet(String eventName) {
        List<Option> groupList = new ArrayList<>();
        List<Option> bossList = new ArrayList<>();
        List<Option> otherList = new ArrayList<>();

        EventTemplateUtil.getEventClass(eventName).ifPresent(eventClass ->
                EventTemplateUtil.copyOperationResourceTemplate(eventClass)
                        .entrySet()
                        .stream()
                        .forEach(entry -> {
                            Option option = new Option();
                            option.setKey(entry.getKey());
                            option.setValue(entry.getKey());
                            option.setLabel(entry.getValue().getOperateName());
                            option.setSort(entry.getValue().getPriority());

                            String resourceKey = entry.getKey();
                            if (resourceKey.startsWith("group")) {
                                groupList.add(option);
                            } else if (resourceKey.startsWith("boss")) {
                                bossList.add(option);
                            } else {
                                otherList.add(option);
                            }
                        }));

        bossList.sort(Comparator.comparing(Option::getSort).reversed());

        List<OptionGroup> groupOptions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupList)) {
            groupOptions.add(new OptionGroup("Monster group", groupList));
        }
        if (!CollectionUtils.isEmpty(bossList)) {
            groupOptions.add(new OptionGroup("Boss", bossList));
        }
        if (!CollectionUtils.isEmpty(otherList)) {
            groupOptions.add(new OptionGroup("Other", otherList));
        }

        return Result.ok(groupOptions);
    }

}

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
        List<Option> resourceExchangeList = new ArrayList<>();
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
                            int addCounter = 0;
                            addCounter += addIfStartsWith(option, groupList, resourceKey.startsWith("group"));
                            addCounter += addIfStartsWith(option, bossList, resourceKey.startsWith("boss"));
                            addCounter += addIfStartsWith(option, resourceExchangeList, resourceKey.startsWith("exchange"));
                            if (addCounter == 0) {
                                otherList.add(option);
                            }
                        }));

        bossList.sort(Comparator.comparing(Option::getSort).reversed());

        List<OptionGroup> groupOptions = new ArrayList<>();
        addIfNotEmpty(groupOptions, groupList, "Monster group");
        addIfNotEmpty(groupOptions, bossList, "Boss");
        addIfNotEmpty(groupOptions, resourceExchangeList, "Resource exchange");
        addIfNotEmpty(groupOptions, otherList, "Other");

        return Result.ok(groupOptions);
    }

    private void addIfNotEmpty(List<OptionGroup> groupOptions, List<Option> options, String label) {
        if (!CollectionUtils.isEmpty(options)) {
            groupOptions.add(new OptionGroup(label, options));
        }
    }

    private int addIfStartsWith(Option option, List<Option> options, boolean startsWith) {
        if (startsWith) {
            options.add(option);
            return 1;
        }
        return 0;
    }

}

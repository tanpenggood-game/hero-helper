package com.itplh.hero.constant;

import com.itplh.hero.domain.Option;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.core.NPCDungeonEvent;
import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.event.core.NPCTimedRefreshEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: tanpenggood
 * @date: 2022-08-21 21:21
 */
@Getter
public enum PickItemEnum {

    青蛇皮(NPCFixedEvent.class, "Fixed", "青蛇-青蛇皮", "青蛇皮"),
    蛇筋(NPCFixedEvent.class, "Fixed", "巨蛇-蛇筋", "蛇筋"),
    牛皮(NPCFixedEvent.class, "Fixed", "老黄牛-牛皮", "牛皮"),
    钥匙(NPCFixedEvent.class, "Fixed", "宝库守卫-钥匙", "钥匙"),
    变异菜青虫毒液(NPCFixedEvent.class, "Fixed", "变异菜青虫毒液", "变异菜青虫毒液"),

    蜂刺(NPCTimedRefreshEvent.class, "Monster group", "桃花阵-蜂刺", "蜂刺"),
    流花河地图(NPCTimedRefreshEvent.class, "Monster group", "流花河-流花河地图", "流花河地图"),
    柳虫皮(NPCTimedRefreshEvent.class, "Monster group", "柳树林-柳虫皮", "柳虫皮"),
    大柳虫皮(NPCTimedRefreshEvent.class, "Monster group", "柳树林-大柳虫皮", "大柳虫皮"),
    老龟壳(NPCTimedRefreshEvent.class, "Monster group", "老龟壳", "老龟壳"),
    大白菜(NPCTimedRefreshEvent.class, "Monster group", "东京菜园-大白菜", "大白菜"),
    景阳岗虎皮(NPCTimedRefreshEvent.class, "Monster group", "景阳岗虎皮", "景阳岗虎皮"),
    蟒蛇皮(NPCTimedRefreshEvent.class, "Monster group", "绿竹林-蟒蛇皮", "蟒蛇皮"),
    蟒蛇胆(NPCTimedRefreshEvent.class, "Monster group", "绿竹林-蟒蛇胆", "蟒蛇胆"),
    电光虾皮(NPCTimedRefreshEvent.class, "Monster group", "幽暗池-电光虾皮", "电光虾皮"),
    电光鱼鳞(NPCTimedRefreshEvent.class, "Monster group", "幽暗池-电光鱼鳞", "电光鱼鳞"),
    美女图谱(NPCTimedRefreshEvent.class, "Monster group", "幽暗池-美女图谱", "美女图谱"),

    青蛇胆(NPCTimedRefreshEvent.class, "Boss", "树洞-青蛇胆", "青蛇胆"),
    蜂王浆(NPCTimedRefreshEvent.class, "Boss", "桃花山-蜂王浆", "蜂王浆"),
    龙皮(NPCTimedRefreshEvent.class, "Boss", "桃花山-龙皮", "龙皮"),
    蒙汗药(NPCTimedRefreshEvent.class, "Boss", "黄泥岗-蒙汗药", "蒙汗药"),

    虎骨(NPCDungeonEvent.class, "Dungeon", "黑风岭-虎骨", "虎骨"),
    太尉秘图(NPCDungeonEvent.class, "Dungeon", "太尉宝库-太尉秘图", "太尉秘图"),
    冰魄珠(NPCDungeonEvent.class, "Dungeon", "冰魄洞-冰魄珠", "冰魄珠"),
    快活令(NPCDungeonEvent.class, "Dungeon", "快活林-快活令", "快活令"),
    快活林烤肉(NPCDungeonEvent.class, "Dungeon", "快活林-快活林烤肉", "快活林烤肉"),
    红宝石(NPCDungeonEvent.class, "Dungeon", "八阵图-红宝石", "红宝石"),
    血印融合丹(NPCDungeonEvent.class, "Dungeon", "地下古城-血印融合丹", "血印融合丹"),
    星光(NPCDungeonEvent.class, "Dungeon", "星光", "星光"),
    赤霄(NPCDungeonEvent.class, "Dungeon", "赤霄", "赤霄"),

    好汉印(null, "Random", "好汉印", "好汉印"),
    训练术(null, "Random", "训练术", "训练术");

    private Class<? extends AbstractEvent> eventClass;
    private String group;
    private String label;
    private String value;

    PickItemEnum(Class<? extends AbstractEvent> eventClass, String group, String label, String value) {
        this.eventClass = eventClass;
        this.group = group;
        this.label = label;
        this.value = value;
    }

    public static Map<String, Collection<Option>> getPickOptionMap(Class<? extends AbstractEvent> eventClass) {
        Map<String, Collection<Option>> pickItemMap = new LinkedHashMap<>();
        for (PickItemEnum pickItemEnum : values()) {
            if (Objects.equals(pickItemEnum.getEventClass(), eventClass)) {
                String group = pickItemEnum.getGroup();
                String label = pickItemEnum.getLabel();
                String value = pickItemEnum.getValue();

                Option option = new Option();
                option.setKey(value);
                option.setLabel(label);
                option.setValue(value);

                if (pickItemMap.containsKey(group)) {
                    pickItemMap.get(group).add(option);
                } else {
                    Collection<Option> pickItems = new ArrayList<>();
                    pickItems.add(option);
                    pickItemMap.put(group, pickItems);
                }
            }
        }
        return pickItemMap;
    }

    public static Collection<String> getRandomPickItems() {
        return getRandomPickOptionMap().getOrDefault("Random", new ArrayList<>())
                .stream()
                .map(Option::getValue)
                .collect(Collectors.toList());
    }

    private static Map<String, Collection<Option>> getRandomPickOptionMap() {
        return getPickOptionMap(null);
    }

}

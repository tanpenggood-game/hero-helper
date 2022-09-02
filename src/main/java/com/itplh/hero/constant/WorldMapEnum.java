package com.itplh.hero.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.itplh.hero.constant.DirectionEnum.*;

@Getter
public enum WorldMapEnum {

    我的庄院("我的庄院", "庄院家丁"),
    资源群山("资源群山", "传送到某资源山房间"),
    史家庄("史家庄", "史家庄丁"),
    少华山("少华山", "少华山石匠"),
    桃花村("桃花村", "桃花村牧童"),
    千年古墓("千年古墓", "守墓老人"),

    桃花山("桃花山", "养蜂人"),
    上东京("上东京", "长须老者"),
    柴家庄("柴家庄", "柴家庄庄客"),
    百兽山岭("百兽山岭", "百兽老人"),

    上沧州("上沧州", "沧州武将"),
    黄泥冈("黄泥冈", "卖酒汉子"),
    去郓城("去郓城", "左:郓城东←"),
    狂盗山谷("狂盗山谷", "狂盗老人"),

    景阳岗("景阳岗", "追星捕快"),
    去沂岭("去沂岭", "沂岭僧人"),
    上梁山("上梁山", "宋江"),
    万剑宝塔("万剑宝塔", "守剑老人"),

    通天塔("通天塔", "八荒剑神"),
    贤才馆("贤才馆", "暂未知"),
    阮家庄("阮家庄", "阮老爷"),
    清风寨("清风寨", "暂未知"),

    黑风岭("黑风岭", "黑风岭砍柴老者"),
    食人谷("食人谷", "进入食人谷副本"),
    冰魄洞("冰魄洞", "冰封老人"),
    太尉宝库("太尉宝库", "进入太尉宝库副本"),

    关胜府("关胜府(181级)", "暂未知"),
    火岩洞("火岩洞", "进入火岩洞副本"),
    幽暗池("幽暗池", "下:幽暗池↓"),
    江都王府("江都王府", "都尉.王生"),

    独角村("独角村", "国子监祭酒.周侗"),
    八卦阵("八卦阵", "无极真人"),
    祝家庄("祝家庄", "祝家老爷"),
    地下古城("地下古城", "暂未知"),
    武家大院("武家大院(232级)", "暂未知"),

    // ===================神行千里扩展地点===================
    练武场("练武场", "田武师", 我的庄院, routers(UP, RIGHT)),
    大柳树("大柳树", "进入树洞", 史家庄, routers(RIGHT, 2, UP, RIGHT, 2)),
    流花河("流花河", "引诱出水龙", 桃花村, routers(RIGHT, 2, UP, RIGHT)),
    食人谷悬崖("食人谷悬崖", "巨蛇", 桃花村, routers(RIGHT, UP, 2, RIGHT)),
    桃花涧("桃花涧", "诱出蜂王", 桃花山, routers(UP, RIGHT, UP, RIGHT, 2)),
    桃花涧_涧底("桃花涧_涧底", "上:桃花涧中央↑", 桃花山, routers(UP, RIGHT, UP, RIGHT, 3, DOWN)),
    水云洞深处("水云洞深处", "上:洞中小路↑", 桃花山, routers(UP, RIGHT, UP, RIGHT, 3, DOWN, RIGHT, DOWN, 2)),
    北大街("北大街", "活动大使", 上东京, routers(UP)),
    东大街_老黄牛("东大街_老黄牛", "老黄牛", 上东京, routers(RIGHT, 2)),
    天字会("天字会", "天字会守卫", 上东京, routers(RIGHT, 2, DOWN, 3)),
    东京_菜园("东京_菜园", "花和尚.鲁智深", 上东京, routers(DOWN, 2, LEFT, 2, DOWN, 2)),
    大菜畦("大菜畦", "诱出变异菜青虫", 东京_菜园, routers(LEFT, 4, DOWN)),
    沧州广场("沧州广场", "阵法大师", 上沧州, routers(RIGHT, 2)),
    沧州_草屋("沧州_草屋", "左:草料场←", 上沧州, routers(RIGHT, 9)),
    黄泥冈_小树林("黄泥冈_小树林", "上:泥土路↑", 黄泥冈, routers(RIGHT, DOWN)),
    景阳岗_树林("景阳岗_树林", "上:景阳岗上↑", 景阳岗, routers(UP, 4)),
    绿竹林("绿竹林", "左:峡谷←", 去沂岭, routers(UP, 5, RIGHT, 2)),
    寒玉狮前厅("寒玉狮前厅", "灵隐和尚", 通天塔, routers(DOWN, 4));

    private String name;
    private String targetPageUniqueLinkName;

    /**
     * 目的地的地图传送点
     */
    private WorldMapEnum transferPoint;
    /**
     * 传送点到目的地的路径
     */
    private List<DirectionEnum> routers;

    WorldMapEnum(String name, String targetPageUniqueLinkName) {
        this.name = name;
        this.targetPageUniqueLinkName = targetPageUniqueLinkName;
    }

    WorldMapEnum(String name, String targetPageUniqueLinkName, WorldMapEnum transferPoint, List<Object> routers) {
        this.name = name;
        this.targetPageUniqueLinkName = targetPageUniqueLinkName;
        this.transferPoint = transferPoint;
        this.routers = parseRouters(routers);
    }

    /**
     * 是否为神行千里扩展地点
     *
     * @return
     */
    public boolean isExtend() {
        return Optional.ofNullable(transferPoint).isPresent();
    }

    private static List routers(Object... values) {
        List<Object> list = new ArrayList<>();
        for (Object value : values) {
            list.add(value);
        }
        return list;
    }

    private List<DirectionEnum> parseRouters(List<Object> routers) {
        List<DirectionEnum> list = new ArrayList<>();
        for (int i = 0; i < routers.size(); i++) {
            Object current = routers.get(i);
            if (current instanceof DirectionEnum) {
                list.add(((DirectionEnum) current));
            } else if (current instanceof Integer && i != 0) {
                int steps = (Integer) current;
                int previousElementIndex = i - 1;
                DirectionEnum direction = (DirectionEnum) routers.get(previousElementIndex);
                IntStream.range(1, steps).forEach(n -> list.add(direction));
            }
        }
        return list;
    }

}

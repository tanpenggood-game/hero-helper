package com.itplh.hero.constant;

import lombok.Getter;

@Getter
public enum DirectionEnum {

    UP("2", "UP", "上↑"),
    DOWN("8", "DOWN", "下↓"),
    LEFT("4", "LEFT", "左←"),
    RIGHT("6", "RIGHT", "右→");

    private String accessValue;
    private String name;
    private String linkName;

    DirectionEnum(String accessValue, String name, String linkName) {
        this.accessValue = accessValue;
        this.name = name;
        this.linkName = linkName;
    }

}

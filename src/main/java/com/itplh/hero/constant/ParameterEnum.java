package com.itplh.hero.constant;

import lombok.Getter;

@Getter
public enum ParameterEnum {

    RESOURCE("resource"),
    NOVICE("novice"),
    SUPPLY_GRAIN("isSupplyGrain");

    private String name;

    ParameterEnum(String name) {
        this.name = name;
    }

}

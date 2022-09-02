package com.itplh.hero.constant;

import lombok.Getter;

@Getter
public enum ParameterEnum {

    /**
     * resources, used to specify the operation object
     */
    RESOURCES("resources"),
    /**
     * isExclude, exclude selected resources {@link ParameterEnum#RESOURCES}, if value is true
     */
    EXCLUDE("isExclude"),
    /**
     * pickItems, used to custom pick item after battle
     */
    PICK_ITEMS("pickItems"),
    /**
     * novice, set value as true, will enable novice protection when operation resource
     */
    NOVICE("novice"),
    /**
     * isSupplyGrain, set value as true, will be supplied grain before operate every resource
     */
    SUPPLY_GRAIN("isSupplyGrain");

    private String name;

    ParameterEnum(String name) {
        this.name = name;
    }

}

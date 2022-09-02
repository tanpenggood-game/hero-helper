package com.itplh.hero.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class OptionGroup {

    private String label;
    private Collection<Option> options;

}

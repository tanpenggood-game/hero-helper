package com.itplh.hero.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OptionGroup {

    private String label;
    private List<Option> options;

}

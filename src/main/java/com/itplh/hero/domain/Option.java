package com.itplh.hero.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Option {

    private String key;
    private String label;
    private String value;
    private String title;
    private boolean disabled;
    @JsonIgnore
    private int sort = 0;

}

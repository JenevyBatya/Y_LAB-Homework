package io.ylab.model;

import io.ylab.managment.enums.ChamberTypeEnum;

public class Chamber {
    private int id;
    private int number;
    private String name;
    private String description;
    private int capacity;
    private ChamberTypeEnum type;

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {
        return capacity;
    }

    public ChamberTypeEnum getType() {
        return type;
    }
}

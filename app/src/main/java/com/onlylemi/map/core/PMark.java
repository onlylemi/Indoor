package com.onlylemi.map.core;

import java.lang.reflect.Field;

/**
 * Created by only乐秘 on 2015-09-23.
 */
public class PMark {

    public float x;
    public float y;
    public String name;

    public PMark() {
    }

    public PMark(float x, float y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    @Override
    public String toString() {
        return "PMark{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                '}';
    }

    public String toJson() {
        String json = "";

        for (Field field : getClass().getDeclaredFields()) {
            json += field.getName();
        }
        return json;
    }


}

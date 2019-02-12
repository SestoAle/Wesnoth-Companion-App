package com.hci.ale.wesnothcompanion.model;

import java.io.Serializable;

/**
 * Created by Ale on 28/01/18.
 */

public class Attack implements Serializable {

    private String desc, type, name, range, icon;
    private int damage, num;

    public Attack(String desc, String type, int damage, int num, String name, String range, String icon){
        this.desc = desc;
        this.type = type;
        this.damage = damage;
        this.num = num;
        this.name = name;
        this.range = range;
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getNum() {
        return num;
    }

    public String getRange() {
        return range;
    }

    public String getIcon() {
        return icon;
    }
}

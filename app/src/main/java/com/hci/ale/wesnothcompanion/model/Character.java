package com.hci.ale.wesnothcompanion.model;

import android.util.Log;

import com.hci.ale.wesnothcompanion.NotesAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Ale on 26/01/18.
 */

public class Character implements Serializable{

    private String id, name, alignment, type, desc, race, image, profile, lvl, flag_icon, advances_to;
    private int exp, max_exp, hp, max_hp, moves, max_moves, attacks_left, max_attacks, side, cost;
    private float x,y;
    private ArrayList<Attack> attacks = new ArrayList<>();
    private String note = "";
    private LinkedHashMap<String, Integer> resistances = new LinkedHashMap<>();
    private LinkedHashMap<String, String> terrains = new LinkedHashMap<>();

    //Attack

    public Character(String id, String name, String alignment, String desc,
                     String race, String lvl, int exp, int max_exp,
                     int hp, int max_hp, int moves, int max_moves, int attacks_left,
                     int max_attacks, String image, String profile, int side, float x, float y,
                     String type, String flag_icon, String advances_to, int cost){

        this.id = id;
        this.name = name;
        this.alignment = alignment;
        this.type = type;
        this.desc = desc;
        this.race = race;
        this.image = image;
        this.profile = profile;
        this.lvl = lvl;
        this.exp = exp;
        this.max_exp = max_exp;
        this.hp = hp;
        this.max_hp = max_hp;
        this.moves = moves;
        this.max_moves = max_moves;
        this.attacks_left = attacks_left;
        this.max_attacks = max_attacks;
        this.side = side;
        this.x = x/General.mapX;

        //Resolve exagon map bug
        if(x%2 == 0){
            this.y = y/General.mapY + (float) General.mapY/General.map_height;
        } else{
            this.y = y/General.mapY;
        }

        if(flag_icon.equals("") || flag_icon == null){
            this.flag_icon = "flags/long-flag-icon.png";
        } else {
            this.flag_icon = flag_icon;
        }
        this.advances_to = advances_to;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlignment() {
        return alignment;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public String getRace() {
        return race;
    }

    public String getImage() {
        return image;
    }

    public String getProfile() {
        return profile;
    }

    public String getLvl() {
        return lvl;
    }

    public int getExp() {
        return exp;
    }

    public int getMax_exp() {
        return max_exp;
    }

    public int getHp() {
        return hp;
    }

    public int getMax_hp() {
        return max_hp;
    }

    public int getMoves() {
        return moves;
    }

    public int getMax_moves() {
        return max_moves;
    }

    public int getAttacks_left() {
        return attacks_left;
    }

    public int getMax_attacks() {
        return max_attacks;
    }

    public int getSide() {
        return side;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void addAttack(Attack attack){
        this.attacks.add(attack);
    }

    public ArrayList<Attack> getAttacks() {
        return attacks;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
        NotesAdapter.getInstance().updateChar(this.name, note, true);
    }

    public void setNoteNoAnim(String note){
        this.note = note;
    }

    public String getFlag_icon() {
        return flag_icon;
    }

    public String[] getAdvances_to() {
        return advances_to.split(",");
    }

    public int getCost() {
        return cost;
    }

    public void addResistance(String key, Integer value){
        resistances.put(key, value);
    }

    public LinkedHashMap<String, Integer> getResistances() {
        return resistances;
    }

    public void addTerrain(String key, String value){
        terrains.put(key, value);
    }

    public LinkedHashMap<String, String> getTerrains() {
        return terrains;
    }
}

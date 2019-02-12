package com.hci.ale.wesnothcompanion.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ale on 02/02/18.
 */


public class General implements Serializable{

    ArrayList<String> victories = new ArrayList<>();
    ArrayList<String> defeats = new ArrayList<>();
    String campaignName;
    int turn_at;
    int max_turn = 0;
    int gold;
    String save_name = "save_db_1";
    static public float mapX = 36;
    static public float mapY = 31;
    static public int map_height;


    private static General ourInstance = new General();

    public static General getInstance() {
        return ourInstance;
    };

    public static void reInstace(){
        ourInstance = new General();
    }

    public General(){};

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setMax_turn(int max_turn) {
        this.max_turn = max_turn;
    }

    public void setTurn_at(int turn_at) {
        this.turn_at = turn_at;
    }

    public void setSave_name(String save_name) {
        this.save_name = save_name;
    }

    public void addVictory(String victory){
        victories.add(victory);
    }

    public void addDefeat(String defeat){
        defeats.add(defeat);
    }

    public ArrayList<String> getDefeats() {
        return defeats;
    }

    public ArrayList<String> getVictories() {
        return victories;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public int getTurn_at() {
        return turn_at;
    }

    public int getMax_turn() {
        return max_turn;
    }

    public int getGold() {
        return gold;
    }

    public String getSave_name() {
        return save_name;
    }

    public static void setMapX(float mapX) {
        General.mapX = mapX;
    }

    public static void setMapY(float mapY) {
        General.mapY = mapY;
    }

    public static void setMap_height(int map_height) {
        General.map_height = map_height;
    }
}

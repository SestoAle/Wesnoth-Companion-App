package com.hci.ale.wesnothcompanion.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Ale on 29/01/18.
 */

public class Notes {

    private String notes = "";
    private LinkedHashMap<String, String> hashChar = new LinkedHashMap<>();

    public Notes() {
    }

    public void updateNotes(String text){
        notes = text;
    }

    public String getNotes() {
        return notes;
    }

    public LinkedHashMap<String, String> getHashChar() {
        return hashChar;
    }
}

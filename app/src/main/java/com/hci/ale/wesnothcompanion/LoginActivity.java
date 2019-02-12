package com.hci.ale.wesnothcompanion;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.hci.ale.wesnothcompanion.model.Utils;

import java.util.List;

//This class is simply a game loader
public class LoginActivity extends AppCompatActivity {

    AlertDialog progress;
    LoadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        final List<String> saves = Utils.listFiles("saves", this);
        adapter = new LoadAdapter(this, saves, progress);
        ListView listView = findViewById(R.id.load_list_view);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onRestart() {
        if(adapter.getProgress() != null){
            adapter.getProgress().dismiss();
            adapter.setProgress(null);
        }
        super.onRestart();
    }
}

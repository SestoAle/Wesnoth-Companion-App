package com.hci.ale.wesnothcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hci.ale.wesnothcompanion.model.Attack;
import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.Utils;

import java.util.ArrayList;
import java.util.Map;

//This class manages the class activity. It shows the class description of the selected character
public class CharacterActivity extends AppCompatActivity {
    ArrayList<Character> characters;
    Character character;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Class");

        LayoutInflater inflater = LayoutInflater.from(this);

        //Take the selected chracter
        character = (Character) getIntent().getSerializableExtra("Character");
        characters = (ArrayList<Character>) getIntent().getSerializableExtra("Characters");

        //Adapt the character's informations to the view
        ((TextView)findViewById(R.id.text_type)).setText(character.getType());

        ((ImageView)findViewById(R.id.character_image)).setImageBitmap(
                Utils.getBitmapFromAssets(character.getProfile(), this));

        LinearLayout adv_container = (LinearLayout) findViewById(R.id.advances_to_container);

        for (final String adv:character.getAdvances_to()) {

            TextView textView = (TextView) inflater.inflate(R.layout.text_view_grid, null);

            final Character tmp = alreadyKnown(adv);

            if(tmp != null){
                //Create link to navigate the class (if already discovered)
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        character = tmp;
                        Intent intent = new Intent(view.getContext(), CharacterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Character", character);
                        intent.putExtra("Characters", characters);
                        view.getContext().startActivity(intent);
                    }
                });
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                textView.setText(getString(R.string.advances_to_known, adv));
            } else if(adv.equals("")){
                textView.setText("");
            } else{
                textView.setText(getString(R.string.advances_to_unknown, adv));
            }
            adv_container.addView(textView);
        }

        ((TextView)findViewById(R.id.race_text)).setText(
                getString(R.string.race, character.getRace()));

        ((TextView)findViewById(R.id.costs_text)).setText(
                getString(R.string.costs,
                        character.getMax_hp(), character.getMax_moves(), character.getCost(),
                        character.getAlignment(), character.getMax_exp()));

        String desc = character.getDesc();

        if(desc.contains("#")){
            desc = desc.substring(0, desc.indexOf("#"));
        }

        ((TextView)findViewById(R.id.desc_text)).setText(desc);

        for (Attack att: character.getAttacks()) {

            GridLayout gridLayout = (GridLayout) inflater.inflate(R.layout.attack_grid_layout, null);

            ((ImageView)gridLayout.findViewById(R.id.attack_profile)).setImageBitmap(Utils.getBitmapFromAssets(att.getIcon(), this));
            ((TextView)gridLayout.findViewById(R.id.attack_name)).setText(att.getName());
            ((TextView)gridLayout.findViewById(R.id.attack_type)).setText(att.getType());
            ((TextView)gridLayout.findViewById(R.id.attack_damage)).setText(att.getDamage() + "-" + att.getNum());
            ((TextView)gridLayout.findViewById(R.id.attack_range)).setText(att.getRange());

            ((ViewGroup)findViewById(R.id.attacks_grid_container)).addView(gridLayout);

        }

        for(Map.Entry<String, Integer> entry : character.getResistances().entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            LinearLayout key_container = (LinearLayout) findViewById(R.id.resistance_type_container);
            TextView key_view = (TextView) inflater.inflate(R.layout.text_view_grid, null);
            key_view.setText(key);
            key_container.addView(key_view);

            LinearLayout value_container = (LinearLayout) findViewById(R.id.resistance_value_container);
            TextView value_key = (TextView) inflater.inflate(R.layout.text_view_grid, null);
            value_key.setText((100 - value) + "%");
            value_key.setTextColor(getResources().getColor(Utils.getPercentageColor(100 - value)));
            value_container.addView(value_key);
        }

        for(Map.Entry<String, String> entry : character.getTerrains().entrySet()) {
            String key = entry.getKey();
            key = key.replace("_", " ");
            String value = entry.getValue();
            value = value.replace("-","");

            LinearLayout key_container = (LinearLayout) findViewById(R.id.terrain_type_container);
            TextView textView = (TextView) inflater.inflate(R.layout.text_view_grid, null);
            textView.setText(key);
            key_container.addView(textView);

            String[] costs = new String[2];

            if(value.length() > 1){
                costs = value.split(",");
            } else{
                costs[0] = "-";
                costs[1] = "100";
            }

            LinearLayout def_container = (LinearLayout) findViewById(R.id.terrain_defense_container);
            textView = (TextView) inflater.inflate(R.layout.text_view_grid, null);
            textView.setText((100 - Integer.parseInt(costs[1])) + "%");
            textView.setTextColor(getResources().getColor(Utils.getPercentageColor(100 - Integer.parseInt(costs[1]))));
            def_container.addView(textView);

            LinearLayout cost_container = (LinearLayout) findViewById(R.id.terrain_cost_container);
            textView = (TextView) inflater.inflate(R.layout.text_view_grid, null);
            textView.setText(costs[0]);
            cost_container.addView(textView);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public Character alreadyKnown(String type){
        for (Character chara: characters) {
            if(chara.getType().equals(type)){
                return chara;
            }
        }
        return null;
    }
}

package com.hci.ale.wesnothcompanion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hci.ale.wesnothcompanion.model.Attack;
import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.Utils;
import com.hci.ale.wesnothcompanion.widget.AnimatedExpandableListView;

import java.util.ArrayList;

/**
 * Created by Ale on 26/01/18.
 */

//Clas to adapt the characters information to the esxpandable list view on the fragment
public class CharactersAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    ArrayList<Character> characters;
    Context context;
    CharactersFragment charactersFragment;

    public CharactersAdapter(CharactersFragment charactersFragment,
                             ArrayList<Character> characters, Context context){
        this.characters = characters;
        this.context = context;
        this.charactersFragment = charactersFragment;
    }

    @Override
    public Object getChild(int i, int i1) {
        return characters.get(i);
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public int getRealChildrenCount(int i) {
        return 1;
    }

    @Override
    public View getRealChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final Character character = characters.get(i);
        LayoutInflater layoutInflater = LayoutInflater.from(context);


        view = layoutInflater.inflate(R.layout.character_child, null);
        EditText editText = view.findViewById(R.id.character_note);
        editText.setText(character.getNote());

        //Update the Note class
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2 > 0) {
                    character.setNote(charSequence.toString());
                } else {
                    character.setNote("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((TextView)view.findViewById(R.id.list_moves)).setText(
                context.getResources().getString(R.string.text_moves, character.getMoves())
        );

        ViewGroup attack_container = view.findViewById(R.id.attack_container);
        attack_container.removeAllViews();

        for(Attack att : character.getAttacks()){

            View textViewAttack = layoutInflater.inflate(R.layout.attack_text_view, null);
            ((TextView)textViewAttack.findViewById(R.id.list_attack_name)).setText(att.getName());
            ((TextView)textViewAttack.findViewById(R.id.list_attack_desc)).setText(
                    context.getResources().getString(R.string.text_attack,
                            att.getDamage(), att.getNum(), att.getRange(), att.getType())
            );

            attack_container.addView(textViewAttack);
        }

        view.findViewById(R.id.clear_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                character.setNote("");
                notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.position_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                charactersFragment.switchPage(character);
            }
        });

        view.findViewById(R.id.more_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().OpenCharacterActivity(character, characters, view.getContext());
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public Object getGroup(int i) {
        return characters.get(i);
    }

    @Override
    public int getGroupCount() {
        return characters.size();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {


        LayoutInflater vi;
        vi = LayoutInflater.from(context);
        convertView = vi.inflate(R.layout.character_group, null);

        Character character = (Character) getGroup(i);

        ImageView charView = convertView.findViewById(R.id.icon_group);
        charView.setImageBitmap(Utils.getBitmapFromAssets(character.getImage(), context));

        ImageView flagView = convertView.findViewById(R.id.flag_icon);
        flagView.setImageBitmap(Utils.getBitmapFromAssets(character.getFlag_icon(), context));
        ColorFilter colorFilterWhite = new LightingColorFilter(0, Color.WHITE);
        flagView.setColorFilter(colorFilterWhite);
        int color = Utils.getResId("side_" + character.getSide(), R.color.class);
        ColorFilter colorFilterSide = new LightingColorFilter(0,
                context.getResources().getColor(color));
        flagView.setColorFilter(colorFilterSide);

        ((TextView)convertView.findViewById(R.id.list_name)).setText(character.getName());

        ((TextView)convertView.findViewById(R.id.list_lvl)).setText(
                context.getResources().getString(R.string.text_lvl, character.getLvl())
        );

        ((ProgressBar)convertView.findViewById(R.id.list_hp)).setProgress(character.getHp());
        ((ProgressBar)convertView.findViewById(R.id.list_hp)).setMax(character.getMax_hp());

        ((ProgressBar)convertView.findViewById(R.id.list_exp)).setProgress(character.getExp());
        ((ProgressBar)convertView.findViewById(R.id.list_exp)).setMax(character.getMax_exp());

        if(character.getNote().length() > 0){
            convertView.findViewById(R.id.edited_icon).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.edited_icon).setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}


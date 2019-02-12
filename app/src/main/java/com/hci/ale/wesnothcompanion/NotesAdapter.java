package com.hci.ale.wesnothcompanion;

import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.Notes;
import com.hci.ale.wesnothcompanion.model.Utils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ale on 29/01/18.
 */

//This class adapt the note informations saved in the model to the app views
public class NotesAdapter {

    private View noteView;
    private Notes notes = new Notes();
    private Context context;
    private CharactersAdapter charactersAdapter;
    private ImageFragment imageFragment;
    private ArrayList<Character> characters;

    private static final NotesAdapter ourInstance = new NotesAdapter();

    public static NotesAdapter getInstance() {
        return ourInstance;
    }

    private NotesAdapter(){}

    public void setContext(Context context) {
        this.context = context;
    }

    public void setImageFragment(ImageFragment imageFragment) {
        this.imageFragment = imageFragment;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public void setCharactersAdapter(CharactersAdapter charactersAdapter) {
        this.charactersAdapter = charactersAdapter;
    }

    public void setNoteView(final View noteView) {
        this.noteView = noteView;
        EditText editText = noteView.findViewById(R.id.textArea_note);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(i2 > 0){
                    notes.updateNotes(charSequence.toString());
                    noteView.findViewById(R.id.clear_general).setVisibility(View.VISIBLE);
                } else {
                    notes.updateNotes("");
                    noteView.findViewById(R.id.clear_general).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        noteView.findViewById(R.id.clear_general).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText)noteView.findViewById(R.id.textArea_note)).setText("");
                notes.updateNotes("");
            }
        });
        updateDrawerChara();
    }

    //This method is called when a note is updated
    public void updateChar(String chara, String text, boolean update_view){

        if(text.length() > 0){
            notes.getHashChar().put(chara, text);
        } else {
            notes.getHashChar().remove(chara);
        }

        if(update_view) {
            updateDrawerChara();
        }
    }

    //This method updates the drawer view
    public void updateDrawerChara(){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final LinearLayout chara_container = noteView.findViewById(R.id.textChara_container);
        chara_container.removeAllViews();

        if (notes.getHashChar().size() <= 0){
            TextView something = (TextView) layoutInflater.inflate(R.layout.write_something_layout, null);
            chara_container.addView(something);
            return;
        }

        for(Character chara: characters) {
            final String key = chara.getName();
            String value = chara.getNote();
            if(value.equals("")){
                Log.d("Yeah", value);
                continue;
            }

            final LinearLayout chara_text = (LinearLayout) layoutInflater.inflate(R.layout.character_note, null);
            ((TextView)chara_text.findViewById(R.id.chara_name_note)).setText(
                    context.getResources().getString(R.string.chara_name_note, key)
            );
            int side = chara.getSide();
            /*for(Character chara: characters){
                if(chara.getName().equals(key)){
                    side = chara.getSide();
                    break;
                }
            }*/
            ((TextView)chara_text.findViewById(R.id.chara_name_note)).setTextColor(
                    context.getResources().getColor(Utils.getResId("side_" + side, R.color.class)));
            ((TextView)chara_text.findViewById(R.id.chara_note)).setText(value);

            chara_container.addView(chara_text);
            chara_text.findViewById(R.id.clear_child).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(final View view) {
                    notes.getHashChar().remove(key);
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.delete_animation);
                    Interpolator interpolator = new LinearOutSlowInInterpolator();
                    anim.setInterpolator(interpolator);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            chara_container.post(new Runnable() {
                                public void run() {
                                    for (Character chara: characters) {
                                        if(chara.getName().equals(key)){
                                            chara.setNote("");
                                            imageFragment.updateBottomSheet(chara);
                                            break;
                                        }
                                    }
                                    charactersAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    chara_text.startAnimation(anim);
                }
            });
        }
    }

    public void restartNote(){
        notes.updateNotes("");
        notes.getHashChar().clear();
    }


}

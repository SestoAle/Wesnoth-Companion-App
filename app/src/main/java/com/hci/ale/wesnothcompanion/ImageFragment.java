package com.hci.ale.wesnothcompanion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.General;
import com.hci.ale.wesnothcompanion.model.Utils;
import com.hci.ale.wesnothcompanion.widget.TouchImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Ale on 23/01/18.
 */

//Fragment that manages the view and the interaction of the map
public class ImageFragment extends Fragment {

    private ArrayList<Character> characters;
    LinearLayout bottom_sheet;
    BottomSheetBehavior bottomSheetBehavior;
    View rootView;
    Character current_character;
    TouchImageView touchImageView;
    View objectiveView;

    int getZoom = 3;
    int cursorUp = 22;
    int cursorBug = 5;
    float moveUp;

    double delta = 0.05;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotesAdapter.getInstance().setImageFragment(this);
        characters = (ArrayList<Character>) getArguments().getSerializable("characters");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        bottom_sheet = (LinearLayout) rootView.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        touchImageView = (TouchImageView) rootView.findViewById(R.id.map_image);
        touchImageView.setImageBitmap(
                Utils.getBitmapFromAssets(General.getInstance().getSave_name() + ".jpg", rootView.getContext()));

        touchImageView.setCursor((ImageView)rootView.findViewById(R.id.cursor));

        touchImageView.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
            }
        });

        //Manage the touch interaction
        touchImageView.setOnTouchListener(new View.OnTouchListener() {

            private final int MAX_CLICK_DURATION = 150;
            private final int MIN_CLICK_DURATION = 50;
            private long startClickTime;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideCursor(view.getRootView().findViewById(R.id.cursor));
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = System.currentTimeMillis();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION && clickDuration > MIN_CLICK_DURATION) {

                            int width = touchImageView.getWidth();
                            int height = touchImageView.getHeight();

                            float t_coord_x = motionEvent.getX()/width;
                            float t_coord_y = motionEvent.getY()/height;

                            RectF rect = touchImageView.getZoomedRect();

                            double current_touch_x = Math.round((t_coord_x*(rect.right-rect.left)+rect.left) * 100.0) / 100.0;
                            double current_touch_y = Math.round((t_coord_y*(rect.bottom-rect.top)+rect.top) * 100.0) / 100.0;

                            double min_distance = 1.0;
                            int min_index = 0;
                            Character chara;

                            for (int i = 0; i < characters.size(); i++) {

                                chara = characters.get(i);

                                double distance = Utils.getInstance().EuclideanDistance(
                                        new PointF(chara.getX(), chara.getY()),
                                        new PointF((float)current_touch_x, (float)current_touch_y));

                                if(distance < min_distance){
                                    min_distance = distance;
                                    min_index = i;
                                }

                            }

                            if(min_distance < delta/touchImageView.getCurrentZoom()){
                                chara = characters.get(min_index);
                                //touchImageView.pubicCancelFLing();
                                openBottomSheet(chara);
                            }
                        }
                    }
                }
                return true;
            }
        });

        rootView.findViewById(R.id.objectives_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED &&
                        bottom_sheet.findViewById(R.id.character_sheet) == null){
                    closeBottomSheet();
                } else {
                    openObjectiveSheet();
                }
            }
        });

        //Initialze and maanage the objective sheet (just one time)
        if(objectiveView == null) {
            objectiveView = inflater.inflate(R.layout.objectives_layout, null);

            ((TextView) objectiveView.findViewById(R.id.text_turn)).setText(
                    getString(R.string.text_turn, General.getInstance().getTurn_at(), General.getInstance().getMax_turn())
            );

            ((TextView) objectiveView.findViewById(R.id.text_gold)).setText(General.getInstance().getGold() + "");

            if (General.getInstance().getVictories().size() < 2) {
                ((TextView)objectiveView.findViewById(R.id.alternative_title)).setText("");
            }

            for (int i = 0; i < General.getInstance().getVictories().size(); i++) {

                String victory = General.getInstance().getVictories().get(i);
                TextView vicotry_text = (TextView) inflater.inflate(R.layout.text_victory_defeat_layout, null);
                vicotry_text.setText(getString(R.string.bullet_objective, victory));

                if (i == 0) {
                    ((ViewGroup) objectiveView.findViewById(R.id.vicotry_container)).addView(vicotry_text);
                } else {
                    ((ViewGroup) objectiveView.findViewById(R.id.alternative_container)).addView(vicotry_text);
                }
            }

            for (int i = 0; i < General.getInstance().getDefeats().size(); i++) {

                String defeat = General.getInstance().getDefeats().get(i);
                TextView vicotry_text = (TextView) inflater.inflate(R.layout.text_victory_defeat_layout, null);
                vicotry_text.setTextColor(getResources().getColor(R.color.textColorDefeat));
                vicotry_text.setText(getString(R.string.bullet_objective, defeat));

                ((ViewGroup) objectiveView.findViewById(R.id.defeat_container)).addView(vicotry_text);

            }

            ((TextView) objectiveView.findViewById(R.id.campagin_title)).setText(General.getInstance().getCampaignName());


            attachKeyboardListeners();
        }

        bottom_sheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation, menu);
    }

    public void closeBottomSheet(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    //Manage the character sheet. This can be initialized several times.
    public void openBottomSheet(final Character character){

        current_character = character;
        ((ViewGroup) bottom_sheet).removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View sheet = layoutInflater.inflate(R.layout.bottom_sheet_layout, null);
        ((ViewGroup) bottom_sheet).addView(sheet);

        ImageView charView = sheet.findViewById(R.id.icon_group);
        charView.setImageBitmap(Utils.getBitmapFromAssets(character.getImage(), getContext()));
        charView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet(character);
            }
        });

        ImageView flagView = sheet.findViewById(R.id.flag_icon);
        flagView.setImageBitmap(Utils.getBitmapFromAssets(character.getFlag_icon(), getContext()));
        ColorFilter colorFilterWhite = new LightingColorFilter(0, Color.WHITE);
        flagView.setColorFilter(colorFilterWhite);
        int color = Utils.getResId("side_" + character.getSide(), R.color.class);
        ColorFilter colorFilterSide = new LightingColorFilter(0,
                getActivity().getResources().getColor(color));
        flagView.setColorFilter(colorFilterSide);

        ((TextView)sheet.findViewById(R.id.list_name)).setText(character.getName());

        ((TextView)sheet.findViewById(R.id.list_lvl)).setText(
                getActivity().getResources().getString(R.string.text_lvl, character.getLvl())
        );

        ((ProgressBar)sheet.findViewById(R.id.list_hp)).setProgress(character.getHp());
        ((ProgressBar)sheet.findViewById(R.id.list_hp)).setMax(character.getMax_hp());

        ((ProgressBar)sheet.findViewById(R.id.list_exp)).setProgress(character.getExp());
        ((ProgressBar)sheet.findViewById(R.id.list_exp)).setMax(character.getMax_exp());

        ((TextView)sheet.findViewById(R.id.moves)).setText(getString(R.string.text_moves, character.getMoves()));

        final EditText editText = (EditText) sheet.findViewById(R.id.character_note);
        editText.setText(character.getNote());

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

        bottom_sheet.findViewById(R.id.clear_child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                character.setNote("");
                editText.setText("");
            }
        });

        bottom_sheet.findViewById(R.id.more_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.getInstance().OpenCharacterActivity(character, characters, view.getContext());
            }
        });

        RectF rectF = touchImageView.getZoomedRect();
        moveUp = (rectF.bottom - rectF.top)/(float)4.5;

        touchImageView.setLast_curr(character.getX(),
                character.getY() + moveUp);
        touchImageView.setScrollPosition(character.getX(),
                character.getY() + moveUp);

        updateCursorPosition((ImageView) getActivity().findViewById(R.id.cursor), character);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void getToThePosition(Character character){
        touchImageView.setZoom(getZoom, character.getX(),
                character.getY() + moveUp);
    }

    //Update the position of the cursor above the selected character
    public void updateCursorPosition(ImageView cursor, Character character){
        cursor.clearAnimation();
        cursor.setScaleX(touchImageView.getCurrentZoom());
        cursor.setScaleY(touchImageView.getCurrentZoom());
        cursor.setVisibility(VISIBLE);
        Animation scaler = AnimationUtils.loadAnimation(getContext(), R.anim.scale_animation);
        scaler.setInterpolator(new LinearOutSlowInInterpolator());
        cursor.startAnimation(scaler);

        RectF rect = touchImageView.getZoomedRect();

        int cursor_left = (int)((character.getX() - rect.left)/(rect.right-rect.left)*touchImageView.getWidth());
        int cursor_top = (int)((character.getY() - rect.top)/(rect.bottom-rect.top)*touchImageView.getHeight());

        Utils.getInstance().SetMargins(cursor,
                cursor_left - (int)(Utils.getInstance().GetDipsFromPixel(cursorBug, getContext())*character.getX()*touchImageView.getCurrentZoom()),
                cursor_top - (int) (Utils.getInstance().GetDipsFromPixel(cursorUp, getContext()) * touchImageView.getCurrentZoom()), 0, 0);
    }

    //If visibile, hide the cursor
    public void hideCursor(View cursor){
        if (cursor.getVisibility() == View.VISIBLE) {
            cursor.clearAnimation();
            cursor.setVisibility(View.GONE);
        }
    }


    //Update the bottom sheet
    public void updateBottomSheet(Character character){
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            if(current_character == character){
                openBottomSheet(character);
            }
        }
    }

    //Open the objectives sheet, initialized at the creation of the fragment
    public void openObjectiveSheet(){
        current_character = null;
        ((ViewGroup) bottom_sheet).removeAllViews();
        touchImageView.setLast_curr(touchImageView.getScrollPosition().x, touchImageView.getScrollPosition().y);
        ((ViewGroup) bottom_sheet).addView(objectiveView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(current_character != null){
            updateBottomSheet(current_character);
        }
    }

    //These methods handle the keyboard bugs with the map
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = touchImageView.getRootView().getHeight() - touchImageView.getHeight();
            int contentViewTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());

            if(heightDiff <= contentViewTop){
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;

    protected void onShowKeyboard(int keyboardHeight) {

        PointF last = touchImageView.getLast_curr();
        touchImageView.setScrollPosition(last.x, last.y);

    }
    protected void onHideKeyboard() {}

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        touchImageView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            touchImageView.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}

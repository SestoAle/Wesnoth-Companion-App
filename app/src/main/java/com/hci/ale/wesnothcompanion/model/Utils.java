package com.hci.ale.wesnothcompanion.model;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.hci.ale.wesnothcompanion.CharacterActivity;
import com.hci.ale.wesnothcompanion.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ale on 01/02/18.
 */

public class Utils {
    private static final Utils ourInstance = new Utils();

    static public int mapX = 33;
    static public int mapY = 41;

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }

    public static Bitmap getBitmapFromAssets(String fileName, Context context){
        AssetManager am = context.getAssets();
        InputStream is = null;

        if(fileName == null ||fileName.equals("")){
            fileName = "unknown.png";
        }

        try{
            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getPercentageColor(int value){

        if(value <= 0){
            return R.color.very_low_percentage;
        }else if(value < 30){
            return R.color.low_percentage;
        }else if(value < 60){
            return R.color.medium_percentage;
        } else{
            return R.color.high_percentage;
        }
    }

    static public List<String> listFiles(String dirFrom, Context context) {

        List<String> saves = new ArrayList<String>();
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = null;
        try {
            fileList = am.list("");
        } catch (IOException e){
        }

        for (String file: fileList) {
            if(file.endsWith(".sqlite")){
                file = file.substring(0, file.indexOf("."));
                saves.add(file);
            }
        }

        return saves;
    }

    public int GetDipsFromPixel(float pixels, Context context)
    {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void OpenCharacterActivity(Character character, ArrayList<Character> characters, Context context){
        Intent intent = new Intent(context, CharacterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Character", character);
        intent.putExtra("Characters", characters);
        context.startActivity(intent);
    }

    public void SetMargins (View v, int l, int t, int r, int b) {

        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public double EuclideanDistance (PointF p1, PointF p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

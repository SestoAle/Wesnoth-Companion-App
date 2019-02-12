package com.hci.ale.wesnothcompanion.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//Class to manage the database and create all the model's objects
public class BaseAdapter implements Serializable{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public BaseAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public BaseAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public BaseAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    //Creater characters
    public ArrayList<Character> getCharacters()
    {

        ArrayList<Character> characters = new ArrayList<>();
        String sql ="SELECT * FROM units WHERE x IS NOT NULL";

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur!=null){
            mCur.moveToFirst();
            for(int i = 0; i < mCur.getCount(); i++){
                Character new_character = new Character(mCur.getString(0), mCur.getString(1),
                        mCur.getString(2), mCur.getString(3), mCur.getString(4),
                        mCur.getString(5), mCur.getInt(6), mCur.getInt(7),
                        mCur.getInt(8), mCur.getInt(9),
                        mCur.getInt(10), mCur.getInt(11), mCur.getInt(12),
                        mCur.getInt(13), mCur.getString(14), mCur.getString(15),
                        mCur.getInt(16),  mCur.getInt(17),
                        mCur.getInt(18),  mCur.getString(19), mCur.getString(20),
                        mCur.getString(21), mCur.getInt(22));
                characters.add(new_character);
                addResistance(new_character);
                addTerrain(new_character);
                mCur.moveToNext();
            }
            mCur.close();
        }

        return characters;
    }

    //Add resistance to character
    public void addResistance(Character character){

        String id = character.getId();
        id = id.replace("'", "''");
        String sql ="SELECT * FROM resistance WHERE _id = '" + id + "'";
        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur!=null) {
            mCur.moveToFirst();
            for(int i = 1; i < mCur.getColumnCount(); i++){

                character.addResistance(mCur.getColumnName(i), mCur.getInt(i));
            }
        }
    }

    //Add terrain modifiers to character
    public void addTerrain(Character character){
        String id = character.getId();
        id = id.replace("'", "''");
        String sql ="SELECT * FROM terrain_modifiers WHERE _id = '" + id + "'";
        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur!=null) {
            mCur.moveToFirst();
            for(int i = 1; i < mCur.getColumnCount(); i++){

                character.addTerrain(mCur.getColumnName(i), mCur.getString(i));
            }
        }
    }

    //Create attacks
    public void getAttacks(ArrayList<Character> characters){

        for (Character chara : characters) {

            String id = chara.getId();
            id = id.replace("'", "''");
            String sql ="SELECT * FROM attacks WHERE _id = '" + id + "'";
            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null) {
                mCur.moveToFirst();
                for(int i = 0; i < mCur.getCount(); i++){

                    Attack attack = new Attack(mCur.getString(1), mCur.getString(2),
                            mCur.getInt(3), mCur.getInt(4), mCur.getString(5),
                            mCur.getString(6), mCur.getString(7));

                    chara.addAttack(attack);
                    mCur.moveToNext();
                }
            }
        }

    }

    //Create objectives and General class
    public void getGeneral(General general){

        String sql ="SELECT * FROM objectives";
        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur!=null){
            mCur.moveToFirst();
            for(int i = 0; i < mCur.getCount(); i++){

                String obj = mCur.getString(0);
                obj = obj.replaceAll("<.*?>", "");
                obj = obj.replaceAll("#.*?wesnoth", "");

                if(mCur.getString(mCur.getColumnIndex("condition")).equals("win")){

                    general.addVictory(obj);

                } else{

                    general.addDefeat(obj);

                }
                mCur.moveToNext();
            }
            mCur.close();
        }

        sql ="SELECT name FROM game";
        mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        General.getInstance().setCampaignName(mCur.getString(0).replace("scenario name^", ""));

        sql ="SELECT turn_at, turns FROM game";
        mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        General.getInstance().setTurn_at(mCur.getInt(0));
        General.getInstance().setMax_turn(mCur.getInt(1));

        sql ="SELECT gold FROM sides WHERE _id = 1";
        mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        General.getInstance().setGold(mCur.getInt(0));

        sql ="SELECT mapX, mapY FROM game";
        mCur = mDb.rawQuery(sql, null);
        mCur.moveToFirst();
        General.setMapX(mCur.getInt(0));
        General.setMapY(mCur.getInt(1)+(float)0.5);
        General.setMap_height(Utils.getBitmapFromAssets(
                General.getInstance().getSave_name()+".jpg", mContext).getHeight());
    }
}
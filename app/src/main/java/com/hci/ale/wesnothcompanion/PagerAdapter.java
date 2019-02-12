package com.hci.ale.wesnothcompanion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hci.ale.wesnothcompanion.model.Character;

import java.util.ArrayList;

/**
 * Created by Ale on 23/01/18.
 */

//Class to manage the ViewPager and the 2 Fragments
public class PagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Character> characters;
    Fragment fragment = null;

    public PagerAdapter(FragmentManager fm, ArrayList<Character> characters) {
        super(fm);
        this.characters = characters;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                fragment = new ImageFragment();
                break;
            case 1:
                fragment = new CharactersFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putSerializable("characters", characters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 0){
            return "Map";
        }

        return "Characters";
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

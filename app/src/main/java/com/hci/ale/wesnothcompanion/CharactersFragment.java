package com.hci.ale.wesnothcompanion;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.Utils;
import com.hci.ale.wesnothcompanion.widget.AnimatedExpandableListView;
import com.hci.ale.wesnothcompanion.widget.TouchImageView;

import java.util.ArrayList;

/**
 * Created by Ale on 24/01/18.
 */

//Fragment that adapts all the characters' informations to the list view
public class CharactersFragment extends Fragment {

    private ArrayList<Character> characters;
    private AnimatedExpandableListView listView;
    private CharactersAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        characters = (ArrayList<Character>) getArguments().getSerializable("characters");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fargment_list, container, false);

        //Set the adapter
        adapter = new CharactersAdapter(this, characters, getActivity().getApplicationContext());
        NotesAdapter.getInstance().setCharactersAdapter(adapter);
        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.characters_list);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        listView.setIndicatorBounds(
                width- Utils.getInstance().GetDipsFromPixel(50, getContext()),
                width-Utils.getInstance().GetDipsFromPixel(5, getContext()));
        listView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if ( view.hasFocus()){
                    view.clearFocus(); //we can put it inside the second if as well, but it makes sense to do it to all scraped views
                    //Optional: also hide keyboard in that case
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        return rootView;
    }

    public void switchPage(Character character){
        ImageFragment page = (ImageFragment)getFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
        page.getToThePosition(character);
        ((ViewPager)getActivity().findViewById(R.id.pager)).setCurrentItem(0);
        page.openBottomSheet(character);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}

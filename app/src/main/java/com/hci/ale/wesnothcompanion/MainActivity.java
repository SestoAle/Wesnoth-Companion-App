package com.hci.ale.wesnothcompanion;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hci.ale.wesnothcompanion.model.BaseAdapter;
import com.hci.ale.wesnothcompanion.model.Character;
import com.hci.ale.wesnothcompanion.model.General;

import java.util.ArrayList;

//Main activity of the application. This class handles and starts all the app components
public class MainActivity extends AppCompatActivity {

    PagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    private ArrayList<Character> characters = null;

    private String save = "save_db_1";

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        save = getIntent().getStringExtra("save");
        drawerLayout = (DrawerLayout) findViewById(R.id.container);

        //Manage the drawer events and interactions (Note view)
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.app_name);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Note");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        //Get the database
        BaseAdapter mDbHelper = new BaseAdapter(this);
        mDbHelper.createDatabase();
        mDbHelper.open();

        //Initialize General
        if(General.getInstance().getMax_turn() == 0){
            mDbHelper.getGeneral(General.getInstance());
        }

        //Initialize Notes and NotesAdapter
        NotesAdapter.getInstance().setContext(this);
        NotesAdapter.getInstance().setNoteView(drawerLayout.findViewById(R.id.note_container));

        //Initialize Characters
        characters = mDbHelper.getCharacters();
        mDbHelper.getAttacks(characters);
        NotesAdapter.getInstance().setCharacters(characters);

        //Initialize ViewPager and the Fragment tabs
        mDemoCollectionPagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), characters);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ((FloatingActionButton)findViewById(R.id.objectives_button)).show();
                } else{
                    ((FloatingActionButton)findViewById(R.id.objectives_button)).setVisibility(View.GONE);
                    ((FloatingActionButton)findViewById(R.id.objectives_button)).hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mDbHelper.close();
    }

    //Manage the back button
    @Override
    public void onBackPressed() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && mViewPager.getCurrentItem() == 0){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            super.onBackPressed();
        }
    }

    public void open_pane(View v){
        drawerLayout.openDrawer(Gravity.END);
    }

}

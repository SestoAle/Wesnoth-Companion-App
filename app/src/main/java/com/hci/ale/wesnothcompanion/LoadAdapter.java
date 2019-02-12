package com.hci.ale.wesnothcompanion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hci.ale.wesnothcompanion.model.DataBaseHelper;
import com.hci.ale.wesnothcompanion.model.General;
import com.hci.ale.wesnothcompanion.model.Utils;

import java.util.List;

/**
 * Created by Ale on 04/02/18.
 */

//This class adapt the General information to the LoadActivity view
public class LoadAdapter extends BaseAdapter {

    private List<String> saves=null;
    private Context context=null;
    AlertDialog progress;

    public LoadAdapter(Context context,List<String> saves, AlertDialog progress)
    {
        this.saves=saves;
        this.context=context;
        this.progress = progress;
    }

    @Override
    public int getCount()
    {
        return saves.size();
    }

    @Override
    public Object getItem(int position)
    {
        return saves.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View v, ViewGroup vg)
    {
        if (v==null)
        {
            v= LayoutInflater.from(context).inflate(R.layout.load_element, null);
        }
        final String save = (String) getItem(position);

        ImageView map_preview = (ImageView) v.findViewById(R.id.save_img);

        map_preview.setImageBitmap(Utils.getBitmapFromAssets(save + ".jpg", v.getContext()));

        ((TextView)v.findViewById(R.id.save_name)).setText(save);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new AlertDialog.Builder(view.getContext()).create();
                progress.setTitle("Load game");
                progress.setMessage("Loading content...");
                progress.show();
                NotesAdapter.getInstance().restartNote();
                DataBaseHelper.DB_NAME = (String)save + ".sqlite";
                General.reInstace();
                General.getInstance().setSave_name(save);
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("save", save);
                view.getContext().startActivity(intent);
            }
        });

        return v;
    }

    public AlertDialog getProgress() {
        return progress;
    }

    public void setProgress(AlertDialog progress) {
        this.progress = progress;
    }
}

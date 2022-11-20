package com.olosnet.simpleflat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.olosnet.simpleflat.database.ProfilesModel;

import java.util.List;

public class ProfileSpinAdapter extends ArrayAdapter<ProfilesModel> {

    private Context ctx;
    private List<ProfilesModel> objects;

    public ProfileSpinAdapter(@NonNull Context context,
                              int resource, @NonNull List<ProfilesModel> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.objects = objects;
    }

    @Override
    public int getCount(){
        return objects.size();
    }

    @Override
    public ProfilesModel getItem(int position){
        return objects.get(position);
    }

    @Override
    public long getItemId(int position){
        return objects.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText(objects.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText(objects.get(position).getName());
        return label;
    }
}

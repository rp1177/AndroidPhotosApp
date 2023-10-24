package com.example.myphotos;

//Aarushi Vashistha and Riddhi Patel

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Thumbnail extends ArrayAdapter<Photo> {
    public Context context;

    public class ViewHolder {
        ImageView photo;
        TextView caption;
        TextView name;
    }


    public Thumbnail(Context context, int resourceId, List<Photo> items) {
        super(context, resourceId, items);
        this.context = context;
    }


    public View getView(int position, View thumbnail, ViewGroup parent) {
        ViewHolder holder;
        Photo photo = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (thumbnail == null) {
            thumbnail = inflater.inflate(R.layout.thumbnail, null);
            holder = new ViewHolder();
            holder.caption = thumbnail.findViewById(R.id.caption);
            holder.photo = thumbnail.findViewById(R.id.photo);
            holder.name = thumbnail.findViewById(R.id.caption);
            thumbnail.setTag(holder);
        } else
            holder = (ViewHolder) thumbnail.getTag();

        holder.caption.setText(photo.cap);
        holder.photo.setImageURI(Uri.parse(photo.photoRef));
        return thumbnail;
    }
}


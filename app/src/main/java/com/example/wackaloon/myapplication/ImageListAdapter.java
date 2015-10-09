package com.example.wackaloon.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Wackaloon on 06.10.2015.
 */
public class ImageListAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private List<ImageItem> imageList;

    public ImageListAdapter(Context context, List<ImageItem>  imageList) {
        super(context, R.layout.layout_block, imageList);

        this.context = context;
        this.imageList = imageList;

        inflater = LayoutInflater.from(context);
    }

    @Override
    // подгрузка изображений и текста из списка для отображения
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = inflater.inflate(R.layout.layout_block, parent, false);
        }

        ImageView image = (ImageView)itemView.findViewById(R.id.imageView);
        TextView text = (TextView)itemView.findViewById(R.id.textView);
        Uri urlImage = Uri.parse(imageList.get(position).getImgUrl());

        Picasso.with(this.context)
                .load(urlImage)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_error)
                .fit()
                .into(image);

        text.setText(imageList.get(position).getImgText());

        return itemView;
    }
}

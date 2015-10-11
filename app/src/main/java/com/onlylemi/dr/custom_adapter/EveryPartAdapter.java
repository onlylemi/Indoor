package com.onlylemi.dr.custom_adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onlylemi.dr.util.AsyncImageLoader;
import com.onlylemi.dr.util.ViewHolder;
import com.onlylemi.indoor.R;
import com.onlylemi.parse.Data;
import com.onlylemi.parse.info.ViewsTable;
import com.onlylemi.user.Assist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 董神 on 2015/9/5.
 * I love programming
 */
public class EveryPartAdapter extends BaseAdapter {
    private Context mContext;
    private AsyncImageLoader asyncImageLoader;
    public List<ViewsTable> viewsTableList;
    public Set<Integer> integerList;

    public EveryPartAdapter(Context mContext) {
        this.mContext = mContext;
        init();
    }
    private void init() {
        asyncImageLoader = new AsyncImageLoader(mContext);
        viewsTableList = new ArrayList<>();
        integerList = new HashSet<>();
        for (int i = 0; i < Data.viewTableList.size(); i++) {
            if (Data.viewTableList.get(i).getPid() == Assist.currentPlaceId) {
                viewsTableList.add(Data.viewTableList.get(i).cloneSelf());
            }
        }
    }

    @Override
    public int getCount() {
        return viewsTableList.size();
    }

    @Override
    public Object getItem(int position) {
        return viewsTableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_main_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_main_adapter_imageview);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.item_main_adapter_textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setTag(viewsTableList.get(position).getImage());
        viewHolder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.user_photo_background));

        Bitmap bitmap = asyncImageLoader.loadImage(viewHolder.imageView, viewsTableList.get(position).getImage());
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(null, bitmap);
            viewHolder.imageView.setBackground(drawable);
        }
        viewHolder.textView.setText(viewsTableList.get(position).getName());
        return convertView;
    }


}

package com.workforce.app.ec.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.workforce.app.ec.R;
import com.workforce.app.ec.holder.MenuItemHolder;

import java.util.ArrayList;
import java.util.List;



public class MenuItemAdapter extends BaseAdapter {
    private List<MenuItemHolder> listItems = new ArrayList<MenuItemHolder>();
    private Context context;
    private Typeface typeface;
    private int iconSize;

    public MenuItemAdapter(Context mContext, List<MenuItemHolder> mListItems, int mIconSize) {
        this.context = mContext;
        this.listItems = mListItems;
        this.iconSize = mIconSize;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listItems.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_menu, arg2, false);
        MenuItemHolder objMenuItem = listItems.get(arg0);
        ImageView imgIcon = (ImageView) rowView.findViewById(R.id.imgIcon);

        imgIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        imgIcon.setImageResource(objMenuItem.getImageResource());
        TextView txtDescription = (TextView) rowView
                .findViewById(R.id.txtDescription);
        txtDescription.setText(objMenuItem.getDescription());
        txtDescription.setTypeface(typeface);
        return rowView;
    }

}

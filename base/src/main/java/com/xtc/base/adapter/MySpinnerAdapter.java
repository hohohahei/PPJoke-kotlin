package com.xtc.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.xtc.base.R;
import com.xtc.base.model.ISpinnerItem;

import java.util.List;

public class MySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {


    private Context context;
    private List<ISpinnerItem> list;

    public MySpinnerAdapter(Context context, List<ISpinnerItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    @Override
    public ISpinnerItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ISpinnerItem iSpinnerItem = list.get(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_spinner, parent, false);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(iSpinnerItem.getSpinnerTitle());
        return convertView;
    }
}

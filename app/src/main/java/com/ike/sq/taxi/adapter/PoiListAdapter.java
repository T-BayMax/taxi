package com.ike.sq.taxi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.ike.sq.taxi.R;

import java.util.List;

public class PoiListAdapter extends BaseAdapter {
    private Context ctx;
    private List<Tip> list;

    public PoiListAdapter(Context context, List<Tip> poiList) {
        this.ctx = context;
        this.list = poiList;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Tip getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(ctx, R.layout.search_poi_listview_item, null);
            holder.poititle = (TextView) convertView
                   .findViewById(R.id.poititle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tip item = list.get(position);
        holder.poititle.setText(item.getName());


        return convertView;
    }
    private class ViewHolder {
        TextView poititle;
    }

}

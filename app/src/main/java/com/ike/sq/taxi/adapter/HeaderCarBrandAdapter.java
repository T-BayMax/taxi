package com.ike.sq.taxi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.CarSonBean;
import com.ike.sq.taxi.bean.City;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public class HeaderCarBrandAdapter extends BaseAdapter {
    private Context context;
    private List<CarBrandBean> hotCities;

    public HeaderCarBrandAdapter(Context context, List<CarBrandBean> list) {
        this.context = context;
        this.hotCities=list;
    }

    @Override
    public int getCount() {
        return hotCities.size();
    }

    @Override
    public Object getItem(int position) {
        return hotCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CarSonAdapter.ViewHolder holder;
        if (null==convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_hot_item, null);
            holder=new CarSonAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder= (CarSonAdapter.ViewHolder) convertView.getTag();
        }
        holder.cityName.setText(hotCities.get(position).getName());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.city_name)
        TextView cityName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

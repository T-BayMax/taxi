package com.ike.sq.taxi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.bean.CarColorBean;
import com.ike.sq.taxi.bean.CarSonBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public class CarColorAdapter extends BaseAdapter {
    private Context context;
    private List<CarColorBean> colorList;

    public CarColorAdapter(Context context, List<CarColorBean> list) {
        this.context = context;
        this.colorList=list;
    }

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return colorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null==convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.view_car_color_item, null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        CarColorBean bean=colorList.get(position);
        holder.ivCarColor.setImageResource(bean.getColorImage());
       holder.cityName.setText(bean.getColorName());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_car_color)
        ImageView ivCarColor;
        @BindView(R.id.tv_car_color_name)
        TextView cityName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

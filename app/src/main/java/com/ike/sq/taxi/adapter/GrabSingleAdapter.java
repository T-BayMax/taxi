package com.ike.sq.taxi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.network.HttpUtils;
import com.ike.sq.taxi.utils.CircleTransform;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.attr.AutoAttr;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by T-BayMax on 2017/5/25.
 */

public class GrabSingleAdapter extends BaseRecyclerAdapter<GrabSingleAdapter.ViewHolder> {
    private List<AroundOrder> list;

    public OnItemClickListener onItemClickListener;
    Context context;
    public GrabSingleAdapter(List<AroundOrder> list,Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_grab_single_list_item, parent, false);
        return new ViewHolder(v, isItem);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,  int position, boolean isItem) {
        if (isItem) {
            final AroundOrder bean=list.get(position);
            holder.tvName.setText(bean.getUserName());
            holder.tvDistance.setText("距离"+bean.getKilometre()+"米");
            holder.tvPlace.setText(bean.getDestination());
            holder.tvGrabSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onGrabSingleClick(holder,bean);
                }
            });
            if (null != bean.getUserPortraitUrl()) {
                Picasso.with(context).load(HttpUtils.IMAGE_RUL + bean.getUserPortraitUrl())
                        .transform(new CircleTransform()).into(holder.ivIcon);
            }
        }
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    public void setData(List<AroundOrder> list,int page) {
        if (page==1) {
            this.list = list;
        }else {
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.tv_place)
        TextView tvPlace;
        @BindView(R.id.tv_grab_single)
        TextView tvGrabSingle;

        public ViewHolder(View view, boolean isItem) {
            super(view);
            if (isItem) {
                ButterKnife.bind(this, itemView);
                AutoUtils.autoSize(itemView, AutoAttr.BASE_HEIGHT);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onGrabSingleClick(ViewHolder viewHolder, AroundOrder o);

    }
}

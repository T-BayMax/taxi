package com.ike.sq.taxi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.ike.sq.taxi.R;
import com.zhy.autolayout.attr.AutoAttr;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by T-BayMax on 2017/5/25.
 */

public class GrabSingleAdapter extends BaseRecyclerAdapter<GrabSingleAdapter.ViewHolder> {
    private List list;

    public OnItemClickListener onItemClickListener;

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
    public void onBindViewHolder(final ViewHolder holder, final int position, boolean isItem) {
        if (isItem) {
            holder.tvGrabSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onGrabSingleClick(holder, list.get(position));
                }
            });
        }
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    public void setData(List list) {
        this.list = list;
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
        public void onGrabSingleClick(ViewHolder viewHolder, Object o);

    }
}

package com.ike.sq.taxi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.bean.AroundOrder;

import java.util.List;


/**
 * 行程记录item
 * <p>
 * Created by T-BayMax on 2017/3/16.
 */

public class TakeTaxiRecordAdapter extends BaseRecyclerAdapter<TakeTaxiRecordAdapter.SimpleAdapterViewHolder> implements View.OnClickListener {

    private List<AroundOrder> list;
    private Context context;
    private int position;

    private OnItemClickListener onItemClickListener;


    public TakeTaxiRecordAdapter(List<AroundOrder> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(SimpleAdapterViewHolder holder, int position, boolean isItem) {
        if (isItem) {


        }


    }

    @Override
    public int getAdapterItemViewType(int position) {
        this.position = position;
        return position;
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    @Override
    public SimpleAdapterViewHolder getViewHolder(View view) {
        return new SimpleAdapterViewHolder(view, false);
    }

    public void setData(List list, int page) {
        if (page == 1) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }

        notifyDataSetChanged();
    }

    @Override
    public SimpleAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_take_taxi_record_item, parent, false);

        SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, isItem);
        return vh;
    }

   /* public void insert(ShareBean person, int position) {
        insert(list, person, position);
    }
*/
    public void remove(int position) {
        remove(list, position);
    }

    public void clear() {
        clear(list);
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {
            case R.id.ll_comment:
                onItemClickListener.onCommentClick(v, (ShareBean) v.getTag());
                break;
            case R.id.ll_like:
                onItemClickListener.onLikeClick(v, (ShareBean) v.getTag());
                break;
            case R.id.ll_item:
                onItemClickListener.onItemClick(v, (ShareBean) v.getTag());
                break;
        }*/
    }

    public class SimpleAdapterViewHolder extends RecyclerView.ViewHolder {


        public SimpleAdapterViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {

            }
        }
    }
/*

    public ShareBean getItem(int position) {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }
*/

    public interface OnItemClickListener {
       /* public void onItemClick(View view, ShareBean bean);

        public void onLikeClick(View view, ShareBean bean);

        public void onCommentClick(View view, ShareBean bean);*/
    }
}
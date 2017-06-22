package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.CommentsBean;
import com.ike.sq.taxi.listeners.OnFeedForCommentListListener;
import com.ike.sq.taxi.listeners.OnTakeTaxiRecordListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 *
 * Created by T-BayMax on 2017/3/20.
 */

public class TakeTaxiRecordModel {
    /**
     * 查看历史记录
     * @param formData
     * @param listener
     */
    public void historicalJourneyData(Map<String, String> formData, final OnTakeTaxiRecordListener listener) {

        HttpUtils.sendGsonPostRequest("/selectArticleComment", formData, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<AroundOrder>>>() {
                }.getType();
                Code<List<AroundOrder>> code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        if (null != code.getData()) {
                            listener.historicalJourneyListener(code.getData());
                        }
                        break;
                    case 0:
                        listener.showError("暂无行程");
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }

}

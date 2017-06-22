package com.ike.sq.taxi;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ike.sq.taxi.db.DBManager;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by T-BayMax on 2017/5/12.
 */

public class App extends MultiDexApplication {
    public static String userId;
    public static Map<String, Activity> activityMap = new HashMap<String, Activity>(0);
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        DBManager.copyDB(getApplicationContext());
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
        mPushAgent.setDebugMode(true);

        handler = new Handler();
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                Log.e("deviceToken", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;

                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {

                switch (msg.builder_id) {
                    case 1:
                        onMsgListener.onDriverMsg(msg);
                        break;
                    default:
                        onMsgListener.onMsg(msg);
                        break;
                }
                return super.getNotification(context, msg);

            }
        };
        mPushAgent.setMessageHandler(messageHandler);
    }

    public static OnMsgListener onMsgListener;

    public static void setOnMsgListener(OnMsgListener onMsg) {
        onMsgListener = onMsg;
    }

    public interface OnMsgListener {
        public void onMsg(UMessage msg);

        public void onDriverMsg(UMessage msg);
    }
}

package com.ike.sq.taxi;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ike.sq.taxi.db.DBManager;
import com.qihoo360.replugin.RePlugin;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by T-BayMax on 2017/5/12.
 */

public class App extends MultiDexApplication {
    public static String userId;
    public static int checkVip;
    public static Map<String, Activity> activityMap = new HashMap<String, Activity>(0);
    private Handler handler;
    public static String deviceToken;

    @Override
    public void onCreate() {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        DBManager.copyDB(getApplicationContext());
/*
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.onAppStart();

        handler = new Handler();
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                Log.e("deviceToken", deviceToken);
                App.this.deviceToken=deviceToken;
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            *//**
             * 自定义消息的回调方法
             * *//*
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

            *//**
             * 自定义通知栏样式的回调方法
             * *//*
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
                String title;
                String text;
                switch (msg.title) {
                    case "2":
                        title ="正在前往目的地";
                        text="";
                        break;

                    case "3":
                        title ="到达目的地";
                        text="";
                        break;
                    default:
                        title = "司机接单";
                        text="";
                        break;
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())//Notification 的兼容类
                        .setSmallIcon(R.mipmap.amap_car)   //若没有设置largeicon，此为左边的大icon，设置了largeicon，则为右下角的小icon，无论怎样，都影响Notifications area显示的图标
                        .setContentTitle(title) //标题
                        .setContentText(text)         //正文
                        .setNumber(1)                       //设置信息条数
                        .setOngoing(true);      //true使notification变为ongoing，用户不能手动清除
                Notification notification=builder.build();

                return notification;

            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        ClassLoader cl=RePlugin.getHostClassLoader();
        if (cl == null) {

        }*/
    }
/*
    public static OnMsgListener onMsgListener;

    public static void setOnMsgListener(OnMsgListener onMsg) {
        onMsgListener = onMsg;
    }

    public interface OnMsgListener {
        public void onMsg(UMessage msg);

        public void onDriverMsg(UMessage msg);
    }*/
}

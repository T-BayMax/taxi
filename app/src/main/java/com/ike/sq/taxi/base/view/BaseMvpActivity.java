package com.ike.sq.taxi.base.view;

import android.os.Bundle;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.zhy.autolayout.AutoLayoutActivity;


/**
 * Created by Min on 2017/3/8.
 * 这个activity基类，主要完成与presenter层的绑定，与解绑
 * 或者一些共有的功能或属性
 * V:实现类要实现的接口类型
 * T：实现类要绑定的Presenter类型
 *
 */

public abstract class BaseMvpActivity<V,T extends BasePersenter<V>> extends AutoLayoutActivity {
    public T presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
             presenter=initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((V)this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }

    public abstract T initPresenter();
}

package com.comvee.tnb.pedometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.comvee.tnb.R;
import com.comvee.tnb.activity.BaseFragment;

/**
 * Created by zhang on 2016/5/25.
 */
public class PedometerFragment extends BaseFragment implements Handler.Callback {
    private long TIME_INTERVAL = 500;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private RoundProgressBar mProgressBar;
    @Override
    public int getViewLayoutId() {
        return R.layout.pedometer_fragment;
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onLaunch(Bundle dataBundle) {
        super.onLaunch(dataBundle);
        init();
        setupService();
    }
    private void setupService() {
        Intent intent = new Intent(getContext(), StepService.class);
        getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getContext().startService(intent);
    }

    private void init() {
        mProgressBar=(RoundProgressBar) findViewById(R.id.hProgressBar);
        delayHandler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                mProgressBar.setProgress(msg.getData().getInt("step"));
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER,TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }
    @Override
    public boolean onBackPress() {
        toBack(null);
        return super.onBackPress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(conn);
    }
}

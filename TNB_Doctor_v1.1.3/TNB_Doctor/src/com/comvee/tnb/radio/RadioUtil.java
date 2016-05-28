package com.comvee.tnb.radio;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.comvee.frame.BaseFragmentActivity;
import com.comvee.frame.FragmentMrg;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.config.ConfigParams;
import com.comvee.tnb.http.TnbBaseNetwork;
import com.comvee.tnb.model.CollectItem;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.model.RadioRoom;
import com.comvee.tnb.model.RadioTurns;
import com.comvee.tnb.network.NetworkCallBack;
import com.comvee.tnb.network.RadioJoinRomm;
import com.comvee.tnb.radio.NickNameWindow.NickNameCallback;
import com.comvee.util.BundleHelper;

import java.util.ArrayList;

public class RadioUtil {

    /**
     * 收藏对象转专辑对象
     **/
    public static RadioAlbum getRadioAlbumByCollect(CollectItem item) {
        RadioAlbum album = new RadioAlbum();

        album.bannerUrl = item.bannerUrl;
        album.updateTime = item.insertDt;
        album.timeLong = item.timeLong;
        album.playUrl = item.playUrl;
        album.photoUrl = item.photoUrl;
        album.radioSubhead = item.radioSubhead;
        album.photoUrl = item.photoUrl;
        album.radioTitle = item.radioTitle;
        album.radioId = item.radioId;
        album.radioInfo = item.radioInfo;
        album.radioId = item.radioId;
        album._id = item.radioId;
        return album;
    }

    /**
     * 专辑对象转节目对象
     **/
    public static RadioAlbumItem getProgramByRadioAlbum(RadioGroup.RadioAlbum items) {
        RadioAlbumItem item = new RadioAlbumItem();
        item._id = items.radioId;
        item.clickNum = items.clickNum;
        item.collectNum = items.collectNum;
        item.pariseNum = items.pariseNum;
        item.photoUrl = items.photoUrl;
        item.playUrl = items.playUrl;
        item.radioId = items.radioId;
        item.radioSubhead = items.radioSubhead;
        item.radioTitle = items.radioTitle;
        item.radioType = items.radioType;
        item.refId = items.radioId;
        item.timeLong = items.timeLong;
        item.updateTime = items.updateTime;
        item.shareHtml = items.shareHtml;
        item.radioSubhead = items.radioSubhead;
        return item;
    }

    /**
     * 轮播图对象转专辑对象
     **/
    public static RadioAlbum getRadioAlbumByTurns(RadioTurns turn) {
        RadioGroup.RadioAlbum album = new RadioGroup.RadioAlbum();
        album.radioId = turn.refId;
        album.bannerUrl = turn.proclamationUrl;
        album.radioTitle = turn.proclamationTitle;
        album.photoUrl = turn.photoUrl;
        album.timeLong = turn.timeLong;
        album.radioSubhead = turn.proclamationSubheading;
        album.updateTime = turn.insertDt;
        album.isCollect = turn.isCollect;
        album.shareHtml = turn.shareHtml;
        album.playUrl = turn.proclamationUrl;
        album.startTime = turn.startTime;
        album.radioType = turn.proclamationType;
        album.bannerUrl = turn.albumBanner;
        album.shareHtml = turn.shareHtml;
        return album;
    }

    public static String getMedaioDurationString(long duration) {
        StringBuffer sb = new StringBuffer();
        long temp = 0;
        temp = duration / (1000 * 60 * 60);
        if (temp > 0) {
            sb.append(temp).append("小时");
        }
        temp = (duration % (1000 * 60 * 60)) / (1000 * 60);
        sb.append(temp).append("分");
        temp = ((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        sb.append(temp).append("秒");
        return sb.toString();
    }

    public static void setReg(int isReg) {
        ConfigParams.setInt(TNBApplication.getInstance(), "isReg", isReg);
    }

    public static int getIsReg() {
        return (int) ConfigParams.getInt(TNBApplication.getInstance(), "isReg");
    }

    public static void jumpFrag(final FragmentActivity act, final RadioGroup.RadioAlbum items) {
        // radioType/1音频节目2视频节目3直播0(专辑)
        // programType/1专辑2节目
        if (items.radioType == 1 || items.radioType == 0) {
            if (items.programType == 1) {// 专辑列表
                FragmentMrg.toFragment(act, ProgrammeListFrag.class, BundleHelper.getBundleByObject(items), true);
            } else if (items.programType == 2) {// 单独节目
                RadioAlbumItem item = RadioUtil.getProgramByRadioAlbum(items);
                ArrayList<RadioAlbumItem> list = new ArrayList<RadioAlbumItem>();
                list.add(item);
                RadioPlayerMrg.getInstance().setDataSource(items, list,false);
                RadioPlayerMrg.getInstance().play(0);
                RadioPlayFrag.toFragment(act, items, item);
            }
        } else if (items.radioType == 3) {
            ((BaseFragmentActivity) act).showProgressDialog(act.getString(R.string.loading));
            RadioJoinRomm net = new RadioJoinRomm();
            net.join(items.startTime, null, items.radioId, new NetworkCallBack() {
                @Override
                public void callBack(int what, int status, Object obj) {
                    ((BaseFragmentActivity) act).cancelProgressDialog();
                    if (status == TnbBaseNetwork.SUCCESS) {
                        RadioRoom radioRoom = (RadioRoom) obj;
                        if (radioRoom.isReg == 1) {
                            if (radioRoom.isSuccess == 1) {
                                HuanxinMainFrag.toFragment(act, radioRoom);
                            } else {
                                items.isSet = radioRoom.isSet;
                                RadioAdvanceFrag.toFragment(act, items);
                            }

                        } else {
                            showInPutDialog(act, items);
                        }

                    }
                }
            });
        }
    }

    public static void showInPutDialog(final FragmentActivity act, final RadioGroup.RadioAlbum items) {
        NickNameWindow window = new NickNameWindow();
        window.show(act.findViewById(R.id.content), new NickNameCallback() {

            @Override
            public void onCancel() {

            }

            @Override
            public void onCallBack(String name) {
                // ===========================提交昵称==========================//

                ((BaseFragmentActivity) act).showProgressDialog("正在提交...");
                RadioJoinRomm net = new RadioJoinRomm();
                net.join(items.startTime, name, items.radioId, new NetworkCallBack() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void callBack(int what, int status, Object obj) {
                        ((BaseFragmentActivity) act).cancelProgressDialog();

                        if (status == 10001 && obj != null) {
                            Toast.makeText(TNBApplication.getInstance(), obj.toString(), Toast.LENGTH_SHORT).show();
                            showInPutDialog(act, items);
                        } else if (obj != null && obj instanceof RadioRoom) {
                            HuanxinMainFrag.toFragment(act, (RadioRoom) obj);
                        }
                    }
                });
                // =================================================================//
            }
        });

    }

    public static void checkJumpComment(FragmentActivity act) {
        try {
            String data = act.getIntent().getDataString();
            if (!TextUtils.isEmpty(data))
                Toast.makeText(act, data, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//
//    public static boolean isAdvanceRemind(String id) {
//        return ConfigParams.getInt(TNBApplication.getInstance(), "radio_remind:" + id) == 1;
//    }
//
//    public static void setAdvanceRemind(String id, boolean isRemind) {
//        ConfigParams.setInt(TNBApplication.getInstance(), "radio_remind:" + id, isRemind ? 1 : 0);
//    }

}

package com.comvee.tnb.radio;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.db.DownloadItemDao;
import com.comvee.tnb.model.RadioAlbumItem;
import com.comvee.tnb.model.RadioGroup.RadioAlbum;
import com.comvee.tnb.network.RadioPlayNextLoader;
import com.comvee.tnb.network.RadioPlayNextLoader.CallBack;
import com.comvee.tool.Log;

public class RadioPlayerMrg {
    private static RadioPlayerMrg mInstance;
    private ArrayList<RadioAlbumItem> mList = new ArrayList<RadioAlbumItem>();
    private RadioAlbum mAlbum;
    private int posistion;
    private MediaPlayer player;

    private PlayerListener mLisener;
    private int mPlayStatus;

    private boolean isLoading;
private boolean isNextPaly=false;
    public static final int STATUS_PAUSE = 1;
    public static final int STATUS_STOP = 2;
    public static final int STATUS_PLAYING = 3;
    private List<RadioAlbumItem> tempList;
    private RadioAlbum tempAlbun;

    public void setPlayerListener(PlayerListener mLisener) {
        this.mLisener = mLisener;
    }

    public void setDataSource(RadioAlbum mAlbum, List<RadioAlbumItem> mList,boolean isNextPlay) {
        this.isNextPaly=isNextPlay;
        this.tempAlbun = mAlbum;
        this.tempList = mList;
    }


    private RadioPlayerMrg() {
        this.player = new MediaPlayer();
        initPlayer(player);
    }

    public MediaPlayer getMediaPlayer() {
        return player;
    }

    public static RadioPlayerMrg getInstance() {
        return mInstance == null ? (mInstance = new RadioPlayerMrg()) : mInstance;
    }

    public static void exitRadio() {
        if (mInstance != null) {
            mInstance.stop();
            mInstance.setPlayerListener(null);
            mInstance = null;
        }
    }

    public void stop() {
        try {
            mPlayStatus = STATUS_STOP;
            player.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean play() {
        try {
            if (mPlayStatus == STATUS_PAUSE || mPlayStatus == STATUS_PLAYING) {

                if (mLisener != null) {
                    mLisener.onStart(getCurrent());
                }
                mList.get(posistion).state = 1;
                player.start();
            } else {
                play(mList.get(posistion));
                mList.get(posistion).state = 1;
            }
            mPlayStatus = STATUS_PLAYING;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void pause() {
        try {
            mPlayStatus = STATUS_PAUSE;
            player.pause();
            mList.get(posistion).state = 2;
            if (mLisener != null) {
                mLisener.onPause(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否有下一首如果没有就下载
     */
    public void checkNext() {
        if (posistion >= mList.size() - 1) {//如果是最后一条
            try {
                RadioPlayNextLoader loader = new RadioPlayNextLoader();
                loader.loadNext(getCurrent().radioId, new CallBack() {
                    @Override
                    public void onCallBack(RadioAlbum album, ArrayList<RadioAlbumItem> list) {

                        if (list != null && !list.isEmpty()) {
                            try {
                                mList.addAll(list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkPre() {

        if (posistion == 0) {
            try {
                RadioPlayNextLoader loader = new RadioPlayNextLoader();
                loader.loadPro(getCurrent().radioId, new CallBack() {
                    @Override
                    public void onCallBack(RadioAlbum album, ArrayList<RadioAlbumItem> list) {

                        if (list != null && !list.isEmpty()) {
                            try {
                                mList.addAll(0, list);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void playNext() {
        if (mPlayStatus == STATUS_PLAYING) {
            player.stop();
        }
        mPlayStatus = STATUS_STOP;
        if (posistion < mList.size() - 1) {
            play(++posistion);
        } else if (mList.size()==1&&!isNextPaly){
            if (mLisener != null) {
                mLisener.onPause(null);
            }
            if (mAlbum != null) {
                try {
                    RadioPlayNextLoader loader = new RadioPlayNextLoader();
                    loader.loadNext(getCurrent().radioId, new CallBack() {
                        @Override
                        public void onCallBack(RadioAlbum album, ArrayList<RadioAlbumItem> list) {
                            if (list != null && !list.isEmpty()) {
                                try {
                                    setDataSource(album, list,false);
                                    play(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (mLisener != null) {
                                        mLisener.onPause(getCurrent());
                                    }
                                }
                            } else {
                                Toast.makeText(TNBApplication.getInstance(), "播放完成", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                posistion = 0;
                mList.clear();
            }

        }else{
            play(0);
        }
    }

    public void playPre() {
        if (mPlayStatus == STATUS_PLAYING) {
            player.stop();
        }
        mPlayStatus = STATUS_STOP;
        if (posistion > 0) {
            play(--posistion);
        }else if (mList.size()==1&&!isNextPaly){
            if (mLisener != null) {
                mLisener.onPause(null);
            }
            if (mAlbum != null) {
                try {
                    RadioPlayNextLoader loader = new RadioPlayNextLoader();
                    loader.loadPro(getCurrent().radioId, new CallBack() {
                        @Override
                        public void onCallBack(RadioAlbum album, ArrayList<RadioAlbumItem> list) {

                            if (list != null && !list.isEmpty()) {
                                try {
                                    setDataSource(album, list,false);
                                    play(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (mLisener != null) {
                                        mLisener.onPause(getCurrent());
                                    }
                                }
                            } else {
                                Toast.makeText(TNBApplication.getInstance(), "播放完成", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                posistion = 0;
                mList.clear();
            }

        }else{
            play(mList.size()-1);
        }
    }

    public void play(RadioAlbumItem obj) {

        obj.state = 1;
        String path = null;
        isLoading = true;
        if (mLisener != null) {
            mLisener.startLoading(obj);
        }
        if (!TextUtils.isEmpty(path = DownloadItemDao.getInstance().getAlreadyDownload(obj.radioId))) {
            Uri url = Uri.parse(DownloadMrg.getDownloadFile(path).getAbsolutePath());
            Log.e(url.toString());
            try {
                // player.stop();
                player.reset();
                player.setDataSource(TNBApplication.getInstance(), url);
                player.prepare();
                mPlayStatus = STATUS_PLAYING;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            path = obj.playUrl;
            Log.e(path);
            try {
                // player.stop();
                player.reset();
                player.setDataSource(path);
                player.prepareAsync();
                mPlayStatus = STATUS_PLAYING;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != mLisener) {
            mLisener.onStart(getCurrent());
        }
    }

    public boolean isPlaying() {
        return mPlayStatus == STATUS_PLAYING || player.isPlaying();
    }

    public void play(int position) {
        if (tempList != null && tempList.size() > position) {
            if (getCurrent() != null && getCurrent().radioId.equals(tempList.get(position).radioId)&&mList==null) {
                // play ();
                tempAlbun = null;
                tempList = null;
                return;
            } else {
                this.mAlbum = tempAlbun;
                if (mList != null) {
                    this.mList.removeAll(this.mList);
                    this.mList.addAll(tempList);
                }
            }
        }

        setAlbumStatu(this.posistion, false);
        setAlbumStatu(position, true);
        this.posistion = position;
        play(mList.get(position));
        tempAlbun = null;
        tempList = null;
    }

    public void setAlbumStatu(int position, boolean playStatus) {
        try {
            if (position > mList.size() - 1) {
                mList.get(position).state = playStatus ? 1 : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isEroor;

    public void initPlayer(final MediaPlayer player) {
        this.player.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                isEroor = true;
                // toast
                return false;
            }
        });

        this.player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!isEroor) {
                    setAlbumStatu(posistion, false);// 将上次的播放状态设置为0
                    playNext();
                }

            }
        });

        this.player.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                isEroor = false;
                if (mLisener != null) {
                    isLoading = false;
                    mLisener.endLoading();
                }
                player.start();
                if (mLisener != null) {
                    mLisener.onStart(getCurrent());
                }
                mPlayStatus = STATUS_PLAYING;

            }
        });

    }

    public boolean isLoading() {
        return isLoading;
    }

    public RadioAlbumItem getCurrent() {
        if (posistion < mList.size())
            return mList.get(posistion);
        else
            return null;
    }

    public RadioAlbum getAlbum() {
        return mAlbum;
    }

    public ArrayList<RadioAlbumItem> getAlbums() {
        return mList;
    }

    public RadioAlbumItem getAudio(int position) {
        if (position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    public int getAudioListSize() {
        return mList.size();
    }

    public static interface PlayerListener {
        public void onPause(RadioAlbumItem item);

        public void onStart(RadioAlbumItem item);

        public void startLoading(RadioAlbumItem obj);

        public void endLoading();
    }
}

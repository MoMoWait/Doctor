package com.comvee.tnb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comvee.tnb.R;
import com.comvee.tnb.TNBApplication;
import com.comvee.tnb.model.NewsListInfo;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

/**
 * 新闻列表条目
 * Created by yujun on 2016/5/3.
 */
public class NewsListAdapter extends BaseAdapter {
    List<NewsListInfo.RowsBean> newsList;
    List<NewsListInfo.RowsBean> oldData;
    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;

    public NewsListAdapter(Context ctx, List<NewsListInfo.RowsBean> items) {
        this.newsList = items;
    }

    public void addData(List<NewsListInfo.RowsBean> list, boolean refresh) {
        int size = list.size();
        if (newsList != null && newsList.size() > 0) {
            if (refresh) {
                for (int i = list.size(); i > 0; i--) {
                    this.newsList.add(0, list.get(--size));
                }
                oldData = newsList;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    this.newsList.add(list.get(i));
                }
            }

        }
    }

    public List getData() {
        return oldData;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > -1) {
            return newsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderMax vHmax = null;
        ViewHolderMini vHmini = null;
        ViewHolderMore vHmore = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ONE:
                    LayoutInflater inflater = (LayoutInflater) TNBApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.news_list_item_mini, null);
                    vHmini = new ViewHolderMini();
                    vHmini.mImageMini = (ImageView) convertView.findViewById(R.id.type_mini_img);
                    vHmini.mTextTitle = (TextView) convertView.findViewById(R.id.type_mini_title);
                    vHmini.mTextText = (TextView) convertView.findViewById(R.id.type_mini_text);
                    vHmini.mTextComment = (TextView) convertView.findViewById(R.id.type_mini_comment);
                    convertView.setTag(vHmini);
                    break;
                case TYPE_TWO:
                    LayoutInflater inflater2 = LayoutInflater.from(TNBApplication.getInstance());
                    convertView = inflater2.inflate(R.layout.news_list_item_max, null);
                    vHmax = new ViewHolderMax();
                    vHmax.mImageMax = (ImageView) convertView.findViewById(R.id.type_max_imag);
                    vHmax.mTextTitle = (TextView) convertView.findViewById(R.id.type_max_title);
                    vHmax.mTextComment = (TextView) convertView.findViewById(R.id.type_max_comment);
                    convertView.setTag(vHmax);
                    break;
                case TYPE_THREE:
                    LayoutInflater inflater3 = LayoutInflater.from(TNBApplication.getInstance());
                    convertView = inflater3.inflate(R.layout.news_list_item_more, null);
                    vHmore = new ViewHolderMore();
                    vHmore.mImageMor1 = (ImageView) convertView.findViewById(R.id.type_more_img1);
                    vHmore.mImageMor2 = (ImageView) convertView.findViewById(R.id.type_more_img2);
                    vHmore.mImageMor3 = (ImageView) convertView.findViewById(R.id.type_more_img3);
                    vHmore.mTextTitle = (TextView) convertView.findViewById(R.id.type_more_title);
                    vHmore.mTextComment = (TextView) convertView.findViewById(R.id.type_more_comment);
                    convertView.setTag(vHmore);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case TYPE_ONE:
                    vHmini = (ViewHolderMini) convertView.getTag();
                    break;
                case TYPE_TWO:
                    vHmax = (ViewHolderMax) convertView.getTag();
                    break;
                case TYPE_THREE:
                    vHmore = (ViewHolderMore) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        switch (type) {
            case TYPE_ONE:
                String url = newsList.get(position).photo_url_list.get(0);
                Glide.with(TNBApplication.getInstance()).load(url).crossFade().into(vHmini.mImageMini);
                vHmini.mTextTitle.setText(newsList.get(position).hot_spot_title);
                vHmini.mTextText.setText(newsList.get(position).abstract_info.trim());
                vHmini.mTextComment.setText(newsList.get(position).clickNum + "阅读");
                break;
            case TYPE_TWO:
                String urlMax = newsList.get(position).photo_url_list.get(0);
                Glide.with(TNBApplication.getInstance()).load(urlMax).crossFade().into(vHmax.mImageMax);
                vHmax.mTextTitle.setText(newsList.get(position).hot_spot_title);
                vHmax.mTextComment.setText(newsList.get(position).clickNum + "阅读");
                break;
            case TYPE_THREE:
                List<String> urlList = newsList.get(position).photo_url_list;
                Glide.with(TNBApplication.getInstance()).load(urlList.get(0)).crossFade().into(vHmore.mImageMor1);
                Glide.with(TNBApplication.getInstance()).load(urlList.get(1)).crossFade().into(vHmore.mImageMor2);
                Glide.with(TNBApplication.getInstance()).load(urlList.get(2)).crossFade().into(vHmore.mImageMor3);
                vHmore.mTextTitle.setText(newsList.get(position).hot_spot_title);
                vHmore.mTextComment.setText(newsList.get(position).clickNum + "阅读");
                break;
            default:
                break;
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        switch (newsList.get(position).photo_size) {
            case 1:
                return TYPE_ONE;//小图
            case 0:
                return TYPE_TWO;//大图
            case 2:
                return TYPE_THREE;//多图
        }
        return TYPE_ONE;
    }

    public class ViewHolderMini {
        TextView mTextText;
        TextView mTextComment;
        ImageView mImageMini;
        TextView mTextTitle;
    }

    public class ViewHolderMax {
        TextView mTextComment;
        ImageView mImageMax;
        TextView mTextTitle;
    }

    public class ViewHolderMore {
        TextView mTextComment;
        ImageView mImageMor1;
        ImageView mImageMor2;
        ImageView mImageMor3;
        TextView mTextTitle;
    }
}

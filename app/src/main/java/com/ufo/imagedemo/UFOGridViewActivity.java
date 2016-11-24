package com.ufo.imagedemo;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ufo.ccphotogridview.CCPhotoGridView;
import com.ufo.ccphotogridview.CCPhotoGridViewAdapter;
import com.ufo.ccphotoview.CCPhotoViewActivity;
import com.ufo.ccphotoview.CCPhotoViewWithDelActivity;
import com.ufo.ccphotoview.CCPhotoViewWithTextActivity;
import com.ufo.ccphotoview.model.CCPhotoModel;

import java.util.ArrayList;
import java.util.UUID;

public class UFOGridViewActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<News> mNewsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ufogrid_view);

        initData();
        initView();
    }


    private void initData() {
        mNewsList = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            mNewsList.add(new News(i));
        }
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview1);
        DemoListAdapter listAdapter = new DemoListAdapter();
        mListView.setAdapter(listAdapter);
    }

    private class DemoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(UFOGridViewActivity.this)
                        .inflate(R.layout.listview_item_ufogridview, null);
                viewHolder = new ViewHolder();
                viewHolder.ufoGridView = (CCPhotoGridView) convertView
                        .findViewById(R.id.ufo_grid_view);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final News news = mNewsList.get(position);
            CCPhotoGridViewAdapter<News> adapter = new CCPhotoGridViewAdapter<News>() {

                @Override
                public int getCount() {
                    return news.Pics.size();
                }

                @Override
                public News getItem(int position) {
                    return news;
                }

                @Override
                public String getImageUrl(int position) {
                    return news.Pics.get(position);
                }

                @Override
                public void onFillImage(String path, ImageView imageView) {
                    Glide.with(UFOGridViewActivity.this)
                            .load(path)
                            .thumbnail(0.1f)
                            .centerCrop()
                            .crossFade()
                            .into(imageView);
                }

                @Override
                public void onItemClick(View view, int index, News t) {

                    Intent intent = new Intent(UFOGridViewActivity.this, CCPhotoViewActivity.class);
                    intent.putExtra(CCPhotoViewActivity.EXTRA_CURRENT_ITEM, index);
                    intent.putExtra(CCPhotoViewActivity.EXTRA_PHOTOS,
                            mNewsList.get(position).Pics);

//                    ArrayList<CCPhotoModel> list = new ArrayList<>();
//                    ArrayList<String> pics = mNewsList.get(position).Pics;
//
//                    for (String pic : pics) {
//                        CCPhotoModel ccPhotoModel = new CCPhotoModel();
//                        ccPhotoModel.setPath(pic);
//                        ccPhotoModel.setDescription("Picture-->" + UUID.randomUUID().toString());
//                        list.add(ccPhotoModel);
//                    }
//
//                    intent.putParcelableArrayListExtra(CCPhotoViewWithTextActivity.CCPHOTOMODEL_EXTRA, list);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view,
                                view.getWidth() / 2, view.getHeight() / 2, view.getWidth(), view.getHeight());
                        startActivity(intent, activityOptionsCompat.toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            };
            viewHolder.ufoGridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            viewHolder.textView.setText(news.Title);
            return convertView;
        }

        class ViewHolder {
            TextView textView;
            CCPhotoGridView ufoGridView;
        }

    }

}

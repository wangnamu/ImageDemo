package com.ufo.imagedemo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ufo.ccphotogridview.CCPhotoGridView;
import com.ufo.ccphotogridview.CCPhotoGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class GridViewNotifyActivity extends AppCompatActivity {


    private List<String> mData;
    private CCPhotoGridViewAdapter<String> mAdapter;

    private Button mButton;
    private CCPhotoGridView mCCPhotoGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view_notify);

        initData();
        initControl();
    }


    private void initData() {
        mData = new ArrayList<>();
        mData.add("http://pic2.to8to.com/case/1512/23/20151223_deb3d3b239679ae695faesf5u5l1omv9_sp.jpg");
    }

    private void initControl() {


        mButton = (Button) findViewById(R.id.button);

        mCCPhotoGridView = (CCPhotoGridView) findViewById(R.id.cc_grid_view);

        mAdapter = new CCPhotoGridViewAdapter<String>() {

            @Override
            public int getCount() {
                return mData.size();
            }

            @Override
            public String getItem(int position) {
                return mData.get(position);
            }

            @Override
            public String getImageUrl(int position) {
                return mData.get(position);
            }

            @Override
            public void onFillImage(String path, ImageView imageView) {
                Glide.with(GridViewNotifyActivity.this)
                        .load(path)
                        .thumbnail(0.1f)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }

            @Override
            public void onItemClick(View view, int index, String t) {

            }
        };

        mCCPhotoGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mData.clear();
                mData.add("http://pic1.to8to.com/case/1512/23/20151223_54197f57a1fbdd389fad721gjgl16n8w_sp.jpg");
                mData.add("http://pic2.to8to.com/case/1512/23/20151223_3bcb35bc9e31eda80c29457ju0l1j0wf_sp.jpg");
                mData.add("http://pic2.to8to.com/case/1512/23/20151223_deb3d3b239679ae695faesf5u5l1omv9_sp.jpg");

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}

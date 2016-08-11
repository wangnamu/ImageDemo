package com.ufo.ccphotoview;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.ufo.ccphotoview.fragment.CCImagePagerFragment;
import com.ufo.ccphotoview.listener.CCPhotoViewLisenter;

import java.util.List;

public class CCPhotoViewActivity extends AppCompatActivity implements CCPhotoViewLisenter {

    public final static String EXTRA_CURRENT_ITEM = "current_item";
    public final static String EXTRA_PHOTOS = "photos";

    protected final static String BACKGROUND_COLOR = "#33000000";


    protected CCImagePagerFragment ccImagePagerFragment;
    protected ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        initContentView();
        initControls();
        initActionBar();
    }


    protected void initContentView() {
        setContentView(R.layout.activity_ccphoto_view);
    }


    protected void initActionBar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(BACKGROUND_COLOR)));
        actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor(BACKGROUND_COLOR)));


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            pageScrolled();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setElevation(25);
            }
        }

    }

    protected void initControls() {

        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);

        initFragment(currentItem, paths);

    }


    protected void initFragment(int currentItem, List<String> paths) {
        ccImagePagerFragment =
                (CCImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        ccImagePagerFragment.setPhotos(paths, currentItem);
        ccImagePagerFragment.setCCPhotoViewLisenter(this);

        ccImagePagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageScrolled();
            }
        });

    }


    public void pageScrolled() {
        if (actionBar != null) actionBar.setTitle(
                getString(R.string.select_image_index, ccImagePagerFragment.getViewPager().getCurrentItem() + 1,
                        ccImagePagerFragment.getPaths().size()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onImageTap(View imageView) {
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }
}

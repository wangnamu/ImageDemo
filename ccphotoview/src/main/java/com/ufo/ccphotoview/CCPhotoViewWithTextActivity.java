package com.ufo.ccphotoview;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.ufo.ccphotoview.model.CCPhotoModel;
import java.util.ArrayList;
import java.util.List;


public class CCPhotoViewWithTextActivity extends CCPhotoViewActivity {

    public static final String CCPHOTOMODEL_EXTRA = "ccphotomodel_extra";

    protected TextView mTextView;
    protected List<CCPhotoModel> mCCPhotoModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.activity_ccphoto_view_with_text);
    }


    @Override
    protected void initControls() {

        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

        mCCPhotoModelList = getIntent().getParcelableArrayListExtra(CCPHOTOMODEL_EXTRA);

        List<String> paths = new ArrayList<>();

        for (CCPhotoModel ccPhotoModel : mCCPhotoModelList) {
            paths.add(ccPhotoModel.getPath());
        }

        initFragment(currentItem, paths);

        mTextView = (TextView) findViewById(R.id.photo_text);

        String description = mCCPhotoModelList.get(currentItem).getDescription();
        mTextView.setText(description);

    }


    @Override
    public void pageScrolled() {
        super.pageScrolled();
        String description = mCCPhotoModelList.get(ccImagePagerFragment.getViewPager().getCurrentItem()).getDescription();
        mTextView.setText(description);
    }



    @Override
    public void onImageTap(View imageView) {
        if (actionBar.isShowing() || mTextView.getVisibility() == View.VISIBLE) {
            actionBar.hide();
            mTextView.setVisibility(View.GONE);
        } else {
            actionBar.show();
            mTextView.setVisibility(View.VISIBLE);
        }
    }

}

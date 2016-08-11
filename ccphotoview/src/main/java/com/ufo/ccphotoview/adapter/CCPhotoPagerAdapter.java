package com.ufo.ccphotoview.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ufo.ccphotoview.R;
import com.ufo.ccphotoview.listener.CCPhotoViewLisenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tjpld on 16/6/21.
 */
public class CCPhotoPagerAdapter extends PagerAdapter {

    private List<String> photos = new ArrayList<>();
    private RequestManager mGlide;

    private CCPhotoViewLisenter mCCPhotoViewLisenter;

    public void setCCPhotoViewLisenter(CCPhotoViewLisenter ccPhotoViewLisenter) {
        this.mCCPhotoViewLisenter = ccPhotoViewLisenter;
    }

    public CCPhotoPagerAdapter(RequestManager glide, List<String> paths) {
        this.photos = paths;
        this.mGlide = glide;
        this.mCCPhotoViewLisenter = null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Context context = container.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.image_pager_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);

        final String path = photos.get(position);
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        mGlide.load(uri)
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_image_placeholder)
                .dontAnimate()
                .dontTransform()
                .override(800, 800)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (context instanceof Activity) {
//                    if (!((Activity) context).isFinishing()) {
//                        ((Activity) context).onBackPressed();
//                    }
//                }

                if (mCCPhotoViewLisenter != null) {
                    mCCPhotoViewLisenter.onImageTap(view);
                }

            }
        });

        container.addView(itemView);

        return itemView;
    }


    @Override
    public int getCount() {
        return photos.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Glide.clear((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}


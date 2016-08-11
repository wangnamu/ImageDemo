package com.ufo.ccphotopreview;

import android.database.DataSetObserver;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by tjpld on 16/7/18.
 */
public abstract class CCPhotoPreviewAdapter<T> {

    private DataSetObserver mDataSetObserver;

    public void registerObserver(DataSetObserver observer) {
        mDataSetObserver = observer;
    }

    public void unregisterObserver() {
        mDataSetObserver = null;
    }

    public void notifyDataSetChanged() {
        if (mDataSetObserver != null) mDataSetObserver.onChanged();
    }

    public void notifyDataSetInvalidate() {
        if (mDataSetObserver != null) mDataSetObserver.onInvalidated();
    }

    public abstract int getCount();

    public abstract T getItem(int position);

    public abstract String getImageUrl(int position);

    public abstract void onFillImage(String path, ImageView imageView);

    public abstract void onItemClick(View view, int index, T t);

    public abstract void onAddClick(View view);

}

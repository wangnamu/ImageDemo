package com.ufo.ccphotopreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by tjpld on 16/7/18.
 */
public class CCPhotoPreview extends ViewGroup {

    public static final int DEFAULT_MAX_SIZE = 9;
    public static final int DEFAULT_COLUMN_NUM = 3;
    public static final int DEFAULT_RATIO = 1;
    public static final int DEFAULT_HORIZONTAL_SPACE = 10;
    public static final int DEFAULT_VERTICAL_SPACE = 10;

    private int numColumns = DEFAULT_COLUMN_NUM;
    private int maxSize = DEFAULT_MAX_SIZE;
    private int horizontalSpacing;
    private int verticalSpacing;
    private float ratio;
    private int childrenWidth;
    private int childrenHeight;

    private CCPhotoPreviewAdapter mAdapter;
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            onDataChange();
        }

    };

    public CCPhotoPreview(Context context) {
        super(context);
        initAttr(context, null);
    }

    public CCPhotoPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public CCPhotoPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes
                    (attrs, R.styleable.CCPhotoPreview);
            numColumns = typedArray.getInteger(R.styleable.CCPhotoPreview_numColumns, DEFAULT_COLUMN_NUM);
            maxSize = typedArray.getInteger(R.styleable.CCPhotoPreview_maxSize, DEFAULT_MAX_SIZE);
            horizontalSpacing = typedArray.
                    getDimensionPixelSize(R.styleable.CCPhotoPreview_horizontalSpacing, DEFAULT_HORIZONTAL_SPACE);
            verticalSpacing = typedArray.
                    getDimensionPixelSize(R.styleable.CCPhotoPreview_verticalSpacing, DEFAULT_VERTICAL_SPACE);
            ratio = typedArray.getFloat(R.styleable.CCPhotoPreview_ratio, DEFAULT_RATIO);
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width, height;

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        width = widthSpecSize;
        height = heightSpecSize;

        int count = getRealCount();
        float rowCount = (count + 0f) / numColumns;
        int realRow = (int) Math.ceil(rowCount);

        childrenWidth = (width - getPaddingLeft() - getPaddingRight()
                - (numColumns - 1) * horizontalSpacing) / numColumns;
        childrenHeight = (int) (childrenWidth * ratio);

        height = getPaddingTop() + getPaddingBottom() + realRow * childrenHeight
                + (realRow - 1) * verticalSpacing;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getRealCount();
        for (int i = 0; i < count; i++) {
            int row = i / numColumns;
            int column = i % numColumns;
            int left = getPaddingLeft() + column * horizontalSpacing + column * childrenWidth;
            int top = getPaddingTop() + row * verticalSpacing + row * childrenHeight;
            View childView = getChildAt(i);
            childView.layout(left, top, left + childrenWidth, top + childrenHeight);
        }
    }


    private void onDataChange() {
        int count = getRealCount();
        int childCount = getChildCount();
        int shortCount = count - childCount;

        if (shortCount > 0) {
            //we need add new subview.
            for (int i = 0; i < shortCount; i++) {
                ImageView imageView = new ImageView(getContext());
                ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                        childrenWidth, childrenHeight
                );
                this.addView(imageView, vlp);
            }

        } else if (shortCount < 0) {
            for (int i = 0; i < Math.abs(shortCount); i++) {
                ImageView imageView = (ImageView) getChildAt(i + count);
                imageView.setVisibility(View.GONE);
            }
        }

        for (int i = 0; i < count; i++) {

            if (i < count - 1) {
                makeImageViewNormal(i);
            } else {

                if (getItemCount() == maxSize) {
                    makeImageViewNormal(i);
                } else {
                    makeImageViewAdd(count);
                }
            }
        }
    }


    private void makeImageViewNormal(int i) {
        final int index = i;
        final ImageView imageView = (ImageView) getChildAt(i);
        imageView.setVisibility(View.VISIBLE);

        mAdapter.onFillImage(mAdapter.getImageUrl(i), imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.onItemClick(imageView, index, mAdapter.getItem(index));
                }
            }
        });
    }

    private void makeImageViewAdd(int count) {
        final ImageView imageView = (ImageView) getChildAt(count - 1);
        imageView.setVisibility(View.VISIBLE);

        imageView.setImageResource(R.drawable.ic_image_add);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.onAddClick(imageView);
                }
            }
        });
    }


    public void setAdapter(CCPhotoPreviewAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.registerObserver(mDataSetObserver);
    }


    public int getRealCount() {
        int count = getItemCount();

        if (count <= maxSize - 1) {
            count = count + 1;
            return count;
        }

        count = Math.min(count, maxSize);
        return count;
    }

    public int getItemCount() {
        if (this.mAdapter != null) {
            return this.mAdapter.getCount();
        } else return 0;
    }


}

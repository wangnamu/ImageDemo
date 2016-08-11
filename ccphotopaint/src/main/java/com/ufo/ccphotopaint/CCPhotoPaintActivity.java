package com.ufo.ccphotopaint;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.ufo.ccphotopaint.fab.FloatingActionButton;
import com.ufo.ccphotopaint.fab.FloatingActionsMenu;
import com.ufo.ccphotopaint.widget.PaintImageListener;
import com.ufo.ccphotopaint.widget.PaintImageView;

import java.io.FileOutputStream;


public class CCPhotoPaintActivity extends AppCompatActivity {

    public final static String EXTRA_WRITE_PATH = "write_path";
    public final static String EXTRA_READ_PATH = "read_path";
    public final static String KEY_WRITE_PATH = "key_path";


    protected FloatingActionsMenu mFloatingActionsMenu;
    protected FloatingActionsMenu mFloatingColorMenu;
    protected PaintImageView mPaintImageView;

    protected FloatingActionButton mClearActionButton;


    protected String mReadPath;
    protected String mWritePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_ccphoto_paint);

        initData();
        initPaintImageView();
        initButton();
        initActionsMenu();
        initColorMenu();

    }

    protected void initData() {
        mReadPath = getIntent().getStringExtra(EXTRA_READ_PATH);
        mWritePath = getIntent().getStringExtra(EXTRA_WRITE_PATH);
    }

    protected void initPaintImageView() {
        mPaintImageView = (PaintImageView) findViewById(R.id.touch_image);
        if (mPaintImageView != null) {
            mPaintImageView.setmImagePath(mReadPath);
            mPaintImageView.setPaintImageListener(paintImageListener);
        }
    }

    protected void initButton() {
        Button closeButton = (Button) findViewById(R.id.btn_close);
        if (closeButton != null) {
            closeButton.setOnClickListener(cancelListener);
        }

        Button nextButton = (Button) findViewById(R.id.btn_next);
        if (nextButton != null) {
            nextButton.setOnClickListener(okListener);
        }

        mClearActionButton = (FloatingActionButton) findViewById(R.id.fab_clear);
        mClearActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaintImageView.clearDraw();
                mClearActionButton.setVisibility(View.GONE);
            }
        });

    }

    protected void initActionsMenu() {

        mFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu_action);

        FloatingActionButton actionDraw = (FloatingActionButton) findViewById(R.id.fab_action_draw);
        if (actionDraw != null) {
            actionDraw.setOnClickListener(actionListener);
        }

        FloatingActionButton actionHand = (FloatingActionButton) findViewById(R.id.fab_action_hand);
        if (actionHand != null) {
            actionHand.setOnClickListener(actionListener);
        }

    }

    protected void initColorMenu() {

        mFloatingColorMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu_color);

        FloatingActionButton red = (FloatingActionButton) findViewById(R.id.fab_color_red);
        if (red != null) {
            red.setOnClickListener(colorListener);
        }

        FloatingActionButton orange = (FloatingActionButton) findViewById(R.id.fab_color_orange);
        if (orange != null) {
            orange.setOnClickListener(colorListener);
        }

        FloatingActionButton yellow = (FloatingActionButton) findViewById(R.id.fab_color_yellow);
        if (yellow != null) {
            yellow.setOnClickListener(colorListener);
        }

        FloatingActionButton green = (FloatingActionButton) findViewById(R.id.fab_color_green);
        if (green != null) {
            green.setOnClickListener(colorListener);
        }

        FloatingActionButton blue = (FloatingActionButton) findViewById(R.id.fab_color_blue);
        if (blue != null) {
            blue.setOnClickListener(colorListener);
        }

    }

    protected String saveToStorage(Bitmap bitmap, String path) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected View.OnClickListener actionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) view;
            mFloatingActionsMenu.collapse();
            mFloatingActionsMenu.setAddButtonIconDrawable(floatingActionButton.getIconDrawable());
            for (int i = 0; i < mFloatingActionsMenu.getChildCount(); i++) {
                mFloatingActionsMenu.getChildAt(i).setVisibility(View.VISIBLE);
            }
            view.setVisibility(View.GONE);

            String state = String.valueOf(view.getTag());

            if (state.equals(getString(R.string.paint_state_touch))) {
                mPaintImageView.setCanDraw(false);
            } else if (state.equals(getString(R.string.paint_state_draw))) {
                mPaintImageView.setCanDraw(true);
            }
        }
    };

    protected View.OnClickListener colorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) view;
            mFloatingColorMenu.collapse();

            mFloatingColorMenu.setAddButtonColor(floatingActionButton.getColorNormal(), floatingActionButton.getColorPressed());
            for (int i = 0; i < mFloatingColorMenu.getChildCount(); i++) {
                mFloatingColorMenu.getChildAt(i).setVisibility(View.VISIBLE);
            }
            view.setVisibility(View.GONE);

            mPaintImageView.setPaintColor(floatingActionButton.getColorNormal());
        }
    };


    protected View.OnClickListener okListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder alertDialog =
                    new AlertDialog.Builder(CCPhotoPaintActivity.this, R.style.LightAlertDialogStyle)
                            .setMessage(getText(R.string.alert_text_message))
                            .setPositiveButton(getText(R.string.alert_text_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveToStorage(mPaintImageView.combineBitmap(), mWritePath);

                                    Intent intent = new Intent();
                                    intent.putExtra(CCPhotoPaintActivity.KEY_WRITE_PATH, mWritePath);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(getText(R.string.alert_text_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

            alertDialog.show();
        }
    };

    protected View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    protected PaintImageListener paintImageListener = new PaintImageListener() {
        @Override
        public void OnDoubleTap(boolean canDraw) {
            if (canDraw) {

                if (mFloatingActionsMenu.isExpanded())
                    mFloatingActionsMenu.collapse();

                FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_action_draw);
                if (floatingActionButton != null) {
                    floatingActionButton.performClick();
                }

            } else {

                if (mFloatingActionsMenu.isExpanded())
                    mFloatingActionsMenu.collapse();

                FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_action_hand);
                if (floatingActionButton != null) {
                    floatingActionButton.performClick();
                }

            }
        }

        @Override
        public void OnDrawing() {

            if (mClearActionButton.getVisibility() != View.VISIBLE) {
                mClearActionButton.setVisibility(View.VISIBLE);
            }

        }

    };

}



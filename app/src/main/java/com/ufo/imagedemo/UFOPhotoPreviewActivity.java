package com.ufo.imagedemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ufo.ccphotopreview.CCPhotoPreview;
import com.ufo.ccphotopreview.CCPhotoPreviewAdapter;
import com.ufo.ccphotoview.CCPhotoViewActivity;
import com.ufo.ccphotoview.CCPhotoViewWithDelActivity;
import com.ufo.ccphotoview.CCPhotoViewWithTextActivity;
import com.ufo.imageutil.CCImageHelper;
import com.ufo.imageutil.CCImageHelperConfig;

import java.io.File;
import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UFOPhotoPreviewActivity extends AppCompatActivity {

    private final static int TAKEPHOTO = 100;
    private final static int SELECTPHOTO = 101;
    private final static int LOOKPHOTO = 102;

    private final static String PATHURL = Environment.getExternalStorageDirectory() + "/ImageDemoTemp/";

    private ArrayList<String> imageUrls;

    private TextView mTextView;
    private CCPhotoPreview mCCPhotoPreview;
    private CCPhotoPreviewAdapter<String> mCCPhotoPreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ufophoto_preview);

        initData();
        initControl();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        UFOPhotoPreviewActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    private void initData() {
        imageUrls = new ArrayList<>();
    }

    private void initControl() {
        mTextView = (TextView) findViewById(R.id.textView);
        mCCPhotoPreview = (CCPhotoPreview) findViewById(R.id.ccphotopreview);

        mCCPhotoPreviewAdapter = new CCPhotoPreviewAdapter<String>() {
            @Override
            public int getCount() {
                return imageUrls.size();
            }

            @Override
            public String getItem(int position) {
                return imageUrls.get(position);
            }

            @Override
            public String getImageUrl(int position) {
                return imageUrls.get(position);
            }

            @Override
            public void onFillImage(String path, ImageView imageView) {

                Glide.with(UFOPhotoPreviewActivity.this).load(path)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .thumbnail(0.1f)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);
            }

            @Override
            public void onItemClick(View view, int index, String s) {

                Intent intent = new Intent(UFOPhotoPreviewActivity.this, CCPhotoViewWithDelActivity.class);
                intent.putExtra(CCPhotoViewActivity.EXTRA_CURRENT_ITEM, index);
                intent.putStringArrayListExtra(CCPhotoViewWithTextActivity.EXTRA_PHOTOS, imageUrls);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view,
                            view.getWidth() / 2, view.getHeight() / 2, view.getWidth(), view.getHeight());

                    ActivityCompat.startActivityForResult(UFOPhotoPreviewActivity.this, intent, LOOKPHOTO, activityOptionsCompat.toBundle());
                } else {
                    startActivityForResult(intent, LOOKPHOTO);
                }


            }

            @Override
            public void onAddClick(View view) {
                new AlertDialog.Builder(UFOPhotoPreviewActivity.this).setItems(new String[]{"相机", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            UFOPhotoPreviewActivityPermissionsDispatcher.takePhotoWithCheck(UFOPhotoPreviewActivity.this);
                        } else {
                            UFOPhotoPreviewActivityPermissionsDispatcher.selectPhotoWithCheck(UFOPhotoPreviewActivity.this);
                        }

                    }
                }).show();
            }
        };

        mCCPhotoPreview.setAdapter(mCCPhotoPreviewAdapter);
        mCCPhotoPreviewAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case TAKEPHOTO: {

                String pathUrl = PATHURL + System.currentTimeMillis() + ".jpg";

                CCImageHelperConfig config = new CCImageHelperConfig();

                String savePath = CCImageHelper
                        .loadWithCompress(getPhotoPath(), config)
                        .rotate()
                        .quality()
                        .saveToStorage(pathUrl);

                imageUrls.add(savePath);

                mCCPhotoPreviewAdapter.notifyDataSetChanged();

                return;
            }
            case SELECTPHOTO: {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String selectedPath = cursor.getString(columnIndex);
                cursor.close();

                String pathUrl = PATHURL + System.currentTimeMillis() + ".jpg";

                CCImageHelperConfig config = new CCImageHelperConfig();

                String savePath = CCImageHelper
                        .loadWithCompress(selectedPath, config)
                        .rotate()
                        .quality()
                        .saveToStorage(pathUrl);

                imageUrls.add(savePath);

                mCCPhotoPreviewAdapter.notifyDataSetChanged();

                return;
            }
            case LOOKPHOTO: {

                imageUrls.clear();
                ArrayList<String> result = data.getStringArrayListExtra(CCPhotoViewWithDelActivity.KEY_SELECTED_PHOTOS);
                imageUrls.addAll(result);

                mCCPhotoPreviewAdapter.notifyDataSetChanged();
            }
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getPhotoPath());
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKEPHOTO);
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTPHOTO);
    }


    private String getPhotoPath() {

        String imageName = "temp.jpg";
        File file = new File(PATHURL);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filename = PATHURL + imageName;

        return filename;

    }


}

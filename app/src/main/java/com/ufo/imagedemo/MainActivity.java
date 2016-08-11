package com.ufo.imagedemo;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ufo.ccphotopaint.CCPhotoPaintActivity;
import com.ufo.imageutil.CCImageHelper;
import com.ufo.imageutil.CCImageHelperConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG = "MainActivity";

    private final static int MY_PERMISSION_CAMERA = 10;
    private final static int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 11;
    private final static int MY_PERMISSION_READ_EXTERNAL_STORAGE = 12;
    private final static int MY_PERMISSION_INTERNET = 13;

    private final static int TAKEPHOTO = 100;
    private final static int SELECTPHOTO = 101;
    private final static int PAINTPHOTO = 102;


    private final static String PATHURL = Environment.getExternalStorageDirectory() + "/ImageDemoTemp/";

    private ListView mListView;
    private List<String> mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initControl();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(ACTIVITY_TAG, "resultCode->" + resultCode);
        Log.d(ACTIVITY_TAG, "requestCode->" + requestCode);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case TAKEPHOTO: {

                String pathUrl = PATHURL + System.currentTimeMillis() + ".jpg";

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = dateFormat.format(new java.util.Date());

                CCImageHelperConfig config = new CCImageHelperConfig();

                String savePath = CCImageHelper
                        .loadWithCompress(getPhotoPath(), config)
                        .rotate()
                        .quality()
                        .watermark(date)
                        .saveToStorage(pathUrl);

                Log.d(ACTIVITY_TAG, savePath);

                Toast.makeText(MainActivity.this, savePath, Toast.LENGTH_SHORT).show();

            }
            break;
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

                Log.d(ACTIVITY_TAG, savePath);

                Toast.makeText(MainActivity.this, savePath, Toast.LENGTH_SHORT).show();

            }
            break;
            case PAINTPHOTO: {
                String pathUrl = PATHURL + System.currentTimeMillis() + ".jpg";

                CCImageHelperConfig config = new CCImageHelperConfig();

                String savePath = CCImageHelper
                        .loadWithCompress(getPhotoPath(), config)
                        .rotate()
                        .quality()
                        .saveToStorage(pathUrl);

                Intent intent = new Intent(MainActivity.this, CCPhotoPaintActivity.class);

                intent.putExtra(CCPhotoPaintActivity.EXTRA_READ_PATH, savePath);
                String writePath = PATHURL + System.currentTimeMillis() + ".jpg";
                intent.putExtra(CCPhotoPaintActivity.EXTRA_WRITE_PATH, writePath);

                startActivity(intent);
            }
            break;
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public final class EasyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listview_item_main, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText(mData.get(position));

            return convertView;
        }
    }

    public final class ViewHolder {
        TextView tv_name;
    }


    private void initData() {
        mData = new ArrayList<>();
        mData.add("相机压缩");
        mData.add("相册压缩");
        mData.add("GridView");
        mData.add("GridViewNotify");
        mData.add("PhotoPreview");
        mData.add("PhotoPaint");
    }

    private void initControl() {
        mListView = (ListView) findViewById(R.id.listView);
        EasyAdapter adapter = new EasyAdapter();
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListView listView = (ListView) parent;
                String name = (String) listView.getItemAtPosition(position);

                switch (name) {
                    case "相机压缩": {
                        MainActivityPermissionsDispatcher.takePhotoWithCheck(MainActivity.this);
                    }
                    break;
                    case "相册压缩": {
                        MainActivityPermissionsDispatcher.selectPhotoWithCheck(MainActivity.this);
                    }
                    break;
                    case "GridView": {
                        Intent intent = new Intent(MainActivity.this, UFOGridViewActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case "GridViewNotify": {
                        Intent intent = new Intent(MainActivity.this, GridViewNotifyActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case "PhotoPreview": {
                        Intent intent = new Intent(MainActivity.this, UFOPhotoPreviewActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case "PhotoPaint": {
                        MainActivityPermissionsDispatcher.paintPhotoWithCheck(MainActivity.this);
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
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


    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void paintPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(getPhotoPath());
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PAINTPHOTO);
    }


    private String getPhotoPath() {

        String imageName = "temp.jpg";
        File file = new File(PATHURL);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filename = PATHURL + imageName;

        Log.d(ACTIVITY_TAG, "filename->" + filename);

        return filename;

    }

}

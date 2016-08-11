package com.ufo.ccphotoview;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by tjpld on 16/6/27.
 */
public class CCPhotoViewWithDelActivity extends CCPhotoViewActivity {


    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuwithdel, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.delete) {
            final int index = ccImagePagerFragment.getCurrentItem();

            final String deletedPath = ccImagePagerFragment.getPaths().get(index);

            Snackbar snackbar = Snackbar.make(ccImagePagerFragment.getView(), R.string.deleted_a_photo,
                    Snackbar.LENGTH_LONG);

            if (ccImagePagerFragment.getPaths().size() <= 1) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.confirm_to_delete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ccImagePagerFragment.getPaths().remove(index);
                                ccImagePagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            } else {

                snackbar.show();

                ccImagePagerFragment.getPaths().remove(index);
                ccImagePagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }

            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ccImagePagerFragment.getPaths().size() > 0) {
                        ccImagePagerFragment.getPaths().add(index, deletedPath);
                    } else {
                        ccImagePagerFragment.getPaths().add(deletedPath);
                    }
                    ccImagePagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                    ccImagePagerFragment.getViewPager().setCurrentItem(index, true);
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra(CCPhotoViewWithDelActivity.KEY_SELECTED_PHOTOS, ccImagePagerFragment.getPaths());
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }


}

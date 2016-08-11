package com.ufo.ccphotoview.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tjpld on 16/6/22.
 */
public class CCPhotoModel implements Parcelable {
    private String path;
    private String description;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(description);

    }

    public static final Creator<CCPhotoModel> CREATOR = new Creator<CCPhotoModel>() {
        @Override
        public CCPhotoModel createFromParcel(Parcel source) {
            CCPhotoModel ccPhotoModel = new CCPhotoModel();
            ccPhotoModel.setPath(source.readString());
            ccPhotoModel.setDescription(source.readString());
            return ccPhotoModel;
        }

        @Override
        public CCPhotoModel[] newArray(int size) {
            return new CCPhotoModel[size];
        }
    };

}

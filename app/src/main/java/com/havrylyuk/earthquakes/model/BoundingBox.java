package com.havrylyuk.earthquakes.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class BoundingBox implements Parcelable {

    private float east;
    private float south;
    private float north;
    private float west;

    public BoundingBox() {
    }

    public BoundingBox(float east, float south, float north, float west) {
        this.east = east;
        this.south = south;
        this.north = north;
        this.west = west;
    }

    protected BoundingBox(Parcel in) {
        east = in.readFloat();
        south = in.readFloat();
        north = in.readFloat();
        west = in.readFloat();
    }

    public static final Creator<BoundingBox> CREATOR = new Creator<BoundingBox>() {
        @Override
        public BoundingBox createFromParcel(Parcel in) {
            return new BoundingBox(in);
        }

        @Override
        public BoundingBox[] newArray(int size) {
            return new BoundingBox[size];
        }
    };

    public float getEast() {
        return east;
    }

    public void setEast(float east) {
        this.east = east;
    }

    public float getSouth() {
        return south;
    }

    public void setSouth(float south) {
        this.south = south;
    }

    public float getNorth() {
        return north;
    }

    public void setNorth(float north) {
        this.north = north;
    }

    public float getWest() {
        return west;
    }

    public void setWest(float west) {
        this.west = west;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(east);
        dest.writeFloat(south);
        dest.writeFloat(north);
        dest.writeFloat(west);
    }
}

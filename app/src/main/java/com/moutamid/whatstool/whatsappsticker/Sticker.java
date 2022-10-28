package com.moutamid.whatstool.whatsappsticker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

class Sticker implements Parcelable {
    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        public Sticker createFromParcel(Parcel parcel) {
            return new Sticker(parcel);
        }

        public Sticker[] newArray(int i) {
            return new Sticker[i];
        }
    };
    List<String> emojis;
    String imageFileName;
    long size;

    public int describeContents() {
        return 0;
    }

    Sticker(String str, List<String> list) {
        this.imageFileName = str;
        this.emojis = list;
    }

    protected Sticker(Parcel parcel) {
        this.imageFileName = parcel.readString();
        this.emojis = parcel.createStringArrayList();
        this.size = parcel.readLong();
    }

    public void setSize(long j) {
        this.size = j;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.imageFileName);
        parcel.writeStringList(this.emojis);
        parcel.writeLong(this.size);
    }
}

package com.moutamid.whatstool.whatsappsticker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class StickerPack implements Parcelable {
    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        public StickerPack createFromParcel(Parcel parcel) {
            return new StickerPack(parcel);
        }

        public StickerPack[] newArray(int i) {
            return new StickerPack[i];
        }
    };
    String androidPlayStoreLink;
    String identifier;
    String iosAppStoreLink;
    private boolean isWhitelisted;
    final String licenseAgreementWebsite;
    String name;
    final String privacyPolicyWebsite;
    String publisher;
    final String publisherEmail;
    final String publisherWebsite;
    private List<Sticker> stickers;
    private long totalSize;
    String trayImageFile;

    public int describeContents() {
        return 0;
    }

    StickerPack(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        this.identifier = str;
        this.name = str2;
        this.publisher = str3;
        this.trayImageFile = str4;
        this.publisherEmail = str5;
        this.publisherWebsite = str6;
        this.privacyPolicyWebsite = str7;
        this.licenseAgreementWebsite = str8;
    }

    public void setIsWhitelisted(boolean z) {
        this.isWhitelisted = z;
    }

    public boolean getIsWhitelisted() {
        return this.isWhitelisted;
    }

    protected StickerPack(Parcel parcel) {
        this.identifier = parcel.readString();
        this.name = parcel.readString();
        this.publisher = parcel.readString();
        this.trayImageFile = parcel.readString();
        this.publisherEmail = parcel.readString();
        this.publisherWebsite = parcel.readString();
        this.privacyPolicyWebsite = parcel.readString();
        this.licenseAgreementWebsite = parcel.readString();
        this.iosAppStoreLink = parcel.readString();
        this.stickers = parcel.createTypedArrayList(Sticker.CREATOR);
        this.totalSize = parcel.readLong();
        this.androidPlayStoreLink = parcel.readString();
        this.isWhitelisted = parcel.readByte() != 0;
    }


    public void setStickers(List<Sticker> list) {
        this.stickers = list;
        this.totalSize = 0;
        for (Sticker sticker : list) {
            this.totalSize += sticker.size;
        }
    }

    public void setAndroidPlayStoreLink(String str) {
        this.androidPlayStoreLink = str;
    }

    public void setIosAppStoreLink(String str) {
        this.iosAppStoreLink = str;
    }

    public List<Sticker> getStickers() {
        return this.stickers;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.identifier);
        parcel.writeString(this.name);
        parcel.writeString(this.publisher);
        parcel.writeString(this.trayImageFile);
        parcel.writeString(this.publisherEmail);
        parcel.writeString(this.publisherWebsite);
        parcel.writeString(this.privacyPolicyWebsite);
        parcel.writeString(this.licenseAgreementWebsite);
        parcel.writeString(this.iosAppStoreLink);
        parcel.writeTypedList(this.stickers);
        parcel.writeLong(this.totalSize);
        parcel.writeString(this.androidPlayStoreLink);
        parcel.writeByte(this.isWhitelisted ? (byte) 1 : 0);
    }
}

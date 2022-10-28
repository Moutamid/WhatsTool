package com.moutamid.whatstool.whatsappsticker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;


import com.moutamid.whatstool.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class StickerPackLoader {
    @NonNull
    public static ArrayList<StickerPack> fetchStickerPacks(Context context) throws IllegalStateException {
        final Cursor cursor = context.getContentResolver().query(StickerContentProvider.AUTHORITYURI, null, null, null, null);
        if (cursor == null) {
            throw new IllegalStateException("could not fetch from content provider, " + BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        }
        HashSet<String> identifierSet = new HashSet<>();
        final ArrayList<StickerPack> stickerPackList = fetchFromContentProvider(cursor);
        for (StickerPack stickerPack : stickerPackList) {
            if (identifierSet.contains(stickerPack.identifier)) {
                throw new IllegalStateException("sticker pack identifiers should be unique, there are more than one pack with identifier:" + stickerPack.identifier);
            } else {
                identifierSet.add(stickerPack.identifier);
            }
        }
        if (stickerPackList.isEmpty()) {
            throw new IllegalStateException("There should be at least one sticker pack in the app");
        }
        for (StickerPack stickerPack : stickerPackList) {
            final List<Sticker> stickers = getStickersForPack(context, stickerPack);
            stickerPack.setStickers(stickers);
            StickerPackValidator.verifyStickerPackValidity(context, stickerPack);
        }
        return stickerPackList;    }

    @NonNull
    private static List<Sticker> getStickersForPack(Context context, StickerPack stickerPack) {
        List<Sticker> fetchFromContentProviderForStickers = fetchFromContentProviderForStickers(stickerPack.identifier, context.getContentResolver());
        for (Sticker next : fetchFromContentProviderForStickers) {
            try {
                byte[] fetchStickerAsset = fetchStickerAsset(stickerPack.identifier, next.imageFileName, context.getContentResolver());
                if (fetchStickerAsset.length > 0) {
                    next.setSize((long) fetchStickerAsset.length);
                } else {
                    throw new IllegalStateException("Asset file is empty, pack: " + stickerPack.name + ", sticker: " + next.imageFileName);
                }
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalStateException("Asset file doesn't exist. pack: " + stickerPack.name + ", sticker: " + next.imageFileName, e);
            }
        }
        return fetchFromContentProviderForStickers;
    }

    @NonNull
    private static ArrayList<StickerPack> fetchFromContentProvider(Cursor cursor) {
        ArrayList<StickerPack> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        do {
            String string = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_IDENTIFIER_IN_QUERY));
            String string2 = cursor.getString(cursor.getColumnIndexOrThrow("sticker_pack_name"));
            String string3 = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_PUBLISHER_IN_QUERY));
            String string4 = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.STICKER_PACK_ICON_IN_QUERY));
            String string5 = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.ANDROID_APP_DOWNLOAD_LINK_IN_QUERY));
            String string6 = cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.IOS_APP_DOWNLOAD_LINK_IN_QUERY));
            StickerPack stickerPack = new StickerPack(string, string2, string3, string4, cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PUBLISHER_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PUBLISHER_WEBSITE)), cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.PRIVACY_POLICY_WEBSITE)), cursor.getString(cursor.getColumnIndexOrThrow(StickerContentProvider.LICENSE_AGREENMENT_WEBSITE)));
            stickerPack.setAndroidPlayStoreLink(string5);
            stickerPack.setIosAppStoreLink(string6);
            arrayList.add(stickerPack);
        } while (cursor.moveToNext());
        return arrayList;
    }

    @NonNull
    private static List<Sticker> fetchFromContentProviderForStickers(String str, ContentResolver contentResolver) {
        Cursor query = contentResolver.query(getStickerListUri(str), new String[]{StickerContentProvider.STICKER_FILE_NAME_IN_QUERY, StickerContentProvider.STICKER_FILE_EMOJI_IN_QUERY}, (String) null, (String[]) null, (String) null);
        ArrayList arrayList = new ArrayList();
        if (query == null || query.getCount() <= 0) {
            if (query != null) {
                query.close();
            }
            return arrayList;
        }
        query.moveToFirst();
        do {
            arrayList.add(new Sticker(query.getString(query.getColumnIndexOrThrow(StickerContentProvider.STICKER_FILE_NAME_IN_QUERY)), Arrays.asList(query.getString(query.getColumnIndexOrThrow(StickerContentProvider.STICKER_FILE_EMOJI_IN_QUERY)).split(","))));
        } while (query.moveToNext());
        if (query != null) {
        }
        return arrayList;
    }


    public static byte[] fetchStickerAsset(@NonNull String str, @NonNull String str2, ContentResolver contentResolver) throws IOException {
        InputStream openInputStream = contentResolver.openInputStream(getStickerAssetUri(str, str2));
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (openInputStream != null) {
                byte[] bArr = new byte[16384];
                while (true) {
                    int read = openInputStream.read(bArr, 0, bArr.length);
                    if (read == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                if (openInputStream != null) {
                    openInputStream.close();
                }
                return byteArray;
            }
        } catch (Throwable th3) {
        }
        return null;
    }

    private static Uri getStickerListUri(String str) {
        return new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("stickers").appendPath(str).build();
    }

    public static Uri getStickerAssetUri(String str, String str2) {
        return new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("stickers_asset").appendPath(str).appendPath(str2).build();
    }
}

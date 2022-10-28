package com.moutamid.whatstool.whatsappsticker;

import android.text.TextUtils;
import android.util.JsonReader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ContentFileParser {
    private static final int LIMIT_EMOJI_COUNT = 2;

    ContentFileParser() {
    }

    @RequiresApi(api = 19)
    @NonNull
    static List<StickerPack> parseStickerPacks(@NonNull InputStream inputStream) throws IOException, IllegalStateException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        List<StickerPack> readStickerPacks = readStickerPacks(jsonReader);
        jsonReader.close();
        return readStickerPacks;
    }

    @NonNull
    private static List<StickerPack> readStickerPacks(@NonNull JsonReader jsonReader) throws IOException, IllegalStateException {
        ArrayList<StickerPack> arrayList = new ArrayList<>();
        jsonReader.beginObject();
        String str = null;
        String str2 = null;
        while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            if (StickerContentProvider.ANDROID_APP_DOWNLOAD_LINK_IN_QUERY.equals(nextName)) {
                str = jsonReader.nextString();
            } else if ("ios_app_store_link".equals(nextName)) {
                str2 = jsonReader.nextString();
            } else if ("sticker_packs".equals(nextName)) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    arrayList.add(readStickerPack(jsonReader));
                }
                jsonReader.endArray();
            } else {
                throw new IllegalStateException("unknown field in json: " + nextName);
            }
        }
        jsonReader.endObject();
        if (!arrayList.isEmpty()) {
            for (StickerPack stickerPack : arrayList) {
                stickerPack.setAndroidPlayStoreLink(str);
                stickerPack.setIosAppStoreLink(str2);
            }
            return arrayList;
        }
        throw new IllegalStateException("sticker pack list cannot be empty");
    }

    @NonNull
    private static StickerPack readStickerPack(@NonNull JsonReader jsonReader) throws IOException, IllegalStateException {
        jsonReader.beginObject();
        List<Sticker> list = null;
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            switch (nextName) {
                case "identifier":
                    str = jsonReader.nextString();
                    break;
                case "privacy_policy_website":
                    str7 = jsonReader.nextString();
                    break;
                case "license_agreement_website":
                    str8 = jsonReader.nextString();
                    break;
                case "name":
                    str2 = jsonReader.nextString();

                    break;
                case "publisher_website":
                    str6 = jsonReader.nextString();
                    break;
                case "tray_image_file":
                    str4 = jsonReader.nextString();
                    break;
                case "publisher_email":
                    str5 = jsonReader.nextString();
                    break;
                case "publisher":
                    str3 = jsonReader.nextString();
                    break;
                case "stickers":
                    list = readStickers(jsonReader);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalStateException("identifier cannot be empty");
        } else if (TextUtils.isEmpty(str2)) {
            throw new IllegalStateException("name cannot be empty");
        } else if (TextUtils.isEmpty(str3)) {
            throw new IllegalStateException("publisher cannot be empty");
        } else if (TextUtils.isEmpty(str4)) {
            throw new IllegalStateException("tray_image_file cannot be empty");
        } else if (list == null || list.isEmpty()) {
            throw new IllegalStateException("sticker list is empty");
        } else if (str.contains("..") || str.contains("/")) {
            throw new IllegalStateException("identifier should not contain .. or / to prevent directory traversal");
        } else {
            jsonReader.endObject();
            StickerPack stickerPack = new StickerPack(str, str2, str3, str4, str5, str6, str7, str8);
            stickerPack.setStickers(list);
            return stickerPack;
        }
    }

    @NonNull
    private static List<Sticker> readStickers(@NonNull JsonReader jsonReader) throws IOException, IllegalStateException {
        jsonReader.beginArray();
        ArrayList arrayList = new ArrayList();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            String str = null;
            ArrayList arrayList2 = new ArrayList(2);
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if ("image_file".equals(nextName)) {
                    str = jsonReader.nextString();
                } else if ("emojis".equals(nextName)) {
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        arrayList2.add(jsonReader.nextString());
                    }
                    jsonReader.endArray();
                } else {
                    throw new IllegalStateException("unknown field in json: " + nextName);
                }
            }
            jsonReader.endObject();
            if (TextUtils.isEmpty(str)) {
                throw new IllegalStateException("sticker image_file cannot be empty");
            } else if (!str.endsWith(".webp")) {
                throw new IllegalStateException("image file for stickers should be webp files, image file is: " + str);
            } else if (str.contains("..") || str.contains("/")) {
                throw new IllegalStateException("the file name should not contain .. or / to prevent directory traversal, image file is:" + str);
            } else {
                arrayList.add(new Sticker(str, arrayList2));
            }
        }
        jsonReader.endArray();
        return arrayList;
    }
}

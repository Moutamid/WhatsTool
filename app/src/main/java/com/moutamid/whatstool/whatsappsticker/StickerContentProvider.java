package com.moutamid.whatstool.whatsappsticker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.moutamid.whatstool.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StickerContentProvider extends ContentProvider {
    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
    public static Uri AUTHORITYURI = new Uri.Builder().scheme("content").authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY).appendPath("metadata").build();
    public static final String CONTENT_FILE_NAME = "contents.json";
    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
    public static final String LICENSE_AGREENMENT_WEBSITE = "sticker_pack_license_agreement_website";
    private static final UriMatcher MATCHER = new UriMatcher(-1);
    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
    public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";
    public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
    private List<StickerPack> stickerPackList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean onCreate() {
        if (BuildConfig.CONTENT_PROVIDER_AUTHORITY.startsWith(((Context) Objects.requireNonNull(getContext())).getPackageName())) {
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "metadata", 1);
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "metadata/*", 2);
            MATCHER.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers/*", 3);
            for (StickerPack next : getStickerPackList()) {
                UriMatcher uriMatcher = MATCHER;
                uriMatcher.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers_asset/" + next.identifier + "/" + next.trayImageFile, 5);
                for (Sticker sticker : next.getStickers()) {
                    UriMatcher uriMatcher2 = MATCHER;
                    uriMatcher2.addURI(BuildConfig.CONTENT_PROVIDER_AUTHORITY, "stickers_asset/" + next.identifier + "/" + sticker.imageFileName, 4);
                }
            }
            return true;
        }
        throw new IllegalStateException("your authority (com.andro.whatswebapp.stickercontentprovider) for the content provider should start with your package name: " + getContext().getPackageName());
    }

    @RequiresApi(api = 19)
    public Cursor query(@NonNull Uri uri, @Nullable String[] strArr, String str, String[] strArr2, String str2) {
        int match = MATCHER.match(uri);
        if (match == 1) {
            return getPackForAllStickerPacks(uri);
        }
        if (match == 2) {
            return getCursorForSingleStickerPack(uri);
        }
        if (match == 3) {
            return getStickersForAStickerPack(uri);
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @RequiresApi(api = 19)
    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String str) {
        int match = MATCHER.match(uri);
        if (match == 4 || match == 5) {
            return getImageAsset(uri);
        }
        return null;
    }

    public String getType(@NonNull Uri uri) {
        int match = MATCHER.match(uri);
        if (match == 1) {
            return "vnd.android.cursor.dir/vnd.com.andro.whatswebapp.stickercontentprovider.metadata";
        }
        if (match == 2) {
            return "vnd.android.cursor.item/vnd.com.andro.whatswebapp.stickercontentprovider.metadata";
        }
        if (match == 3) {
            return "vnd.android.cursor.dir/vnd.com.andro.whatswebapp.stickercontentprovider.stickers";
        }
        if (match == 4) {
            return "image/webp";
        }
        if (match == 5) {
            return "image/png";
        }
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @RequiresApi(api = 19)
    private synchronized void readContentFile(@NonNull Context context) {
        InputStream open;
        try {
            open = context.getAssets().open(CONTENT_FILE_NAME);
            this.stickerPackList = ContentFileParser.parseStickerPacks(open);
            if (open != null) {
                open.close();
            }
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("contents.json file has some issues: " + e.getMessage(), e);
        } catch (Throwable th) {
        }
        return;
    }

    @RequiresApi(api = 19)
    public List<StickerPack> getStickerPackList() {
        if (this.stickerPackList == null) {
            readContentFile((Context) Objects.requireNonNull(getContext()));
        }
        return this.stickerPackList;
    }

    private Cursor getPackForAllStickerPacks(@NonNull Uri uri) {
        return getStickerPackInfo(uri, getStickerPackList());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Cursor getCursorForSingleStickerPack(@NonNull Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        for (StickerPack next : getStickerPackList()) {
            if (lastPathSegment.equals(next.identifier)) {
                return getStickerPackInfo(uri, Collections.singletonList(next));
            }
        }
        return getStickerPackInfo(uri, new ArrayList());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    private Cursor getStickerPackInfo(@NonNull Uri uri, @NonNull List<StickerPack> list) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{STICKER_PACK_IDENTIFIER_IN_QUERY, "sticker_pack_name", STICKER_PACK_PUBLISHER_IN_QUERY, STICKER_PACK_ICON_IN_QUERY, ANDROID_APP_DOWNLOAD_LINK_IN_QUERY, IOS_APP_DOWNLOAD_LINK_IN_QUERY, PUBLISHER_EMAIL, PUBLISHER_WEBSITE, PRIVACY_POLICY_WEBSITE, LICENSE_AGREENMENT_WEBSITE});
        for (StickerPack next : list) {
            MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
            newRow.add(next.identifier);
            newRow.add(next.name);
            newRow.add(next.publisher);
            newRow.add(next.trayImageFile);
            newRow.add(next.androidPlayStoreLink);
            newRow.add(next.iosAppStoreLink);
            newRow.add(next.publisherEmail);
            newRow.add(next.publisherWebsite);
            newRow.add(next.privacyPolicyWebsite);
            newRow.add(next.licenseAgreementWebsite);
        }
        matrixCursor.setNotificationUri(((Context) Objects.requireNonNull(getContext())).getContentResolver(), uri);
        return matrixCursor;
    }

    @RequiresApi(api = 19)
    @NonNull
    private Cursor getStickersForAStickerPack(@NonNull Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{STICKER_FILE_NAME_IN_QUERY, STICKER_FILE_EMOJI_IN_QUERY});
        for (StickerPack next : getStickerPackList()) {
            if (lastPathSegment.equals(next.identifier)) {
                for (Sticker next2 : next.getStickers()) {
                    matrixCursor.addRow(new Object[]{next2.imageFileName, TextUtils.join(",", next2.emojis)});
                }
            }
        }
        matrixCursor.setNotificationUri(((Context) Objects.requireNonNull(getContext())).getContentResolver(), uri);
        return matrixCursor;
    }

    @RequiresApi(api = 19)
    private AssetFileDescriptor getImageAsset(Uri uri) throws IllegalArgumentException {
        AssetManager assets = ((Context) Objects.requireNonNull(getContext())).getAssets();
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 3) {
            String str = pathSegments.get(pathSegments.size() - 1);
            String str2 = pathSegments.get(pathSegments.size() - 2);
            if (TextUtils.isEmpty(str2)) {
                throw new IllegalArgumentException("identifier is empty, uri: " + uri);
            } else if (!TextUtils.isEmpty(str)) {
                for (StickerPack next : getStickerPackList()) {
                    if (str2.equals(next.identifier)) {
                        if (str.equals(next.trayImageFile)) {
                            return fetchFile(uri, assets, str, str2);
                        }
                        for (Sticker sticker : next.getStickers()) {
                            if (str.equals(sticker.imageFileName)) {
                                return fetchFile(uri, assets, str, str2);
                            }
                        }
                        continue;
                    }
                }
                return null;
            } else {
                throw new IllegalArgumentException("file name is empty, uri: " + uri);
            }
        } else {
            throw new IllegalArgumentException("path segments should be 3, uri is: " + uri);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private AssetFileDescriptor fetchFile(@NonNull Uri uri, @NonNull AssetManager assetManager, @NonNull String str, @NonNull String str2) {
        try {
            return assetManager.openFd(str2 + "/" + str);
        } catch (IOException e) {
            String packageName = ((Context) Objects.requireNonNull(getContext())).getPackageName();
            Log.e(packageName, "IOException when getting asset file, uri:" + uri, e);
            return null;
        }
    }

    public int delete(@NonNull Uri uri, @Nullable String str, String[] strArr) {
        throw new UnsupportedOperationException("Not supported");
    }

    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Not supported");
    }

    public int update(@NonNull Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not supported");
    }
}

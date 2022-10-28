package com.moutamid.whatstool.whatsappsticker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.moutamid.whatstool.BuildConfig;

public class WhitelistCheck {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    public static final String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    private static final String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    private static final String QUERY_PATH = "is_whitelisted";
    private static final String QUERY_RESULT_COLUMN_NAME = "result";
    public static final String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";
    private static final String STICKER_APP_AUTHORITY = BuildConfig.CONTENT_PROVIDER_AUTHORITY;

    static boolean isWhitelisted(@NonNull Context context, @NonNull String str) {
        try {
            boolean isWhitelistedFromProvider = isWhitelistedFromProvider(context, str, CONSUMER_WHATSAPP_PACKAGE_NAME);
            boolean isWhitelistedFromProvider2 = isWhitelistedFromProvider(context, str, SMB_WHATSAPP_PACKAGE_NAME);
            if (!isWhitelistedFromProvider || !isWhitelistedFromProvider2) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }


    private static boolean isWhitelistedFromProvider(@NonNull Context context, @NonNull String str, String str2) {
        PackageManager packageManager = context.getPackageManager();
        if (!isPackageInstalled(str2, packageManager)) {
            return true;
        }
        String str3 = str2 + CONTENT_PROVIDER;
        boolean z = false;
        if (packageManager.resolveContentProvider(str3, 128) == null) {
            return false;
        }
        Cursor query = context.getContentResolver().query(new Uri.Builder().scheme("content").authority(str3).appendPath(QUERY_PATH).appendQueryParameter(AUTHORITY_QUERY_PARAM, STICKER_APP_AUTHORITY).appendQueryParameter(IDENTIFIER_QUERY_PARAM, str).build(), (String[]) null, (String) null, (String[]) null, (String) null);
        if (query != null) {
            if (query.moveToFirst()) {
                if (query.getInt(query.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME)) == 1) {
                    z = true;
                }
                query.close();
                return z;
            }
            query.close();

        }
        return false;
    }

    public static boolean isPackageInstalled(String str, PackageManager packageManager) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            unused.printStackTrace();
        }
        return false;
    }
}

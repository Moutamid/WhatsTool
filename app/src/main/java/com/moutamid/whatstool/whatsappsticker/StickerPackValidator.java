package com.moutamid.whatstool.whatsappsticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class StickerPackValidator {
    private static final String APPLE_STORE_DOMAIN = "itunes.apple.com";
    private static final String PLAY_STORE_DOMAIN = "play.google.com";

    public static void verifyStickerPackValidity(@NonNull Context context, @NonNull StickerPack stickerPack) throws IllegalStateException {
        if (TextUtils.isEmpty(stickerPack.identifier)) {
            throw new IllegalStateException("sticker pack identifier is empty");
        } else if (stickerPack.identifier.length() <= 128) {
            checkStringValidity(stickerPack.identifier);
            if (TextUtils.isEmpty(stickerPack.publisher)) {
                throw new IllegalStateException("sticker pack publisher is empty, sticker pack identifier:" + stickerPack.identifier);
            } else if (stickerPack.publisher.length() > 128) {
                throw new IllegalStateException("sticker pack publisher cannot exceed 128 characters, sticker pack identifier:" + stickerPack.identifier);
            } else if (TextUtils.isEmpty(stickerPack.name)) {
                throw new IllegalStateException("sticker pack name is empty, sticker pack identifier:" + stickerPack.identifier);
            } else if (stickerPack.name.length() > 128) {
                throw new IllegalStateException("sticker pack name cannot exceed 128 characters, sticker pack identifier:" + stickerPack.identifier);
            } else if (TextUtils.isEmpty(stickerPack.trayImageFile)) {
                throw new IllegalStateException("sticker pack tray id is empty, sticker pack identifier:" + stickerPack.identifier);
            } else if (!TextUtils.isEmpty(stickerPack.androidPlayStoreLink) && !isValidWebsiteUrl(stickerPack.androidPlayStoreLink)) {
                throw new IllegalStateException("Make sure to include http or https in url links, android play store link is not strings valid url: " + stickerPack.androidPlayStoreLink);
            } else if (!TextUtils.isEmpty(stickerPack.androidPlayStoreLink) && !isURLInCorrectDomain(stickerPack.androidPlayStoreLink, PLAY_STORE_DOMAIN)) {
                throw new IllegalStateException("android play store link should use play store domain: play.google.com");
            } else if (!TextUtils.isEmpty(stickerPack.iosAppStoreLink) && !isValidWebsiteUrl(stickerPack.iosAppStoreLink)) {
                throw new IllegalStateException("Make sure to include http or https in url links, ios app store link is not strings valid url: " + stickerPack.iosAppStoreLink);
            } else if (!TextUtils.isEmpty(stickerPack.iosAppStoreLink) && !isURLInCorrectDomain(stickerPack.iosAppStoreLink, APPLE_STORE_DOMAIN)) {
                throw new IllegalStateException("iOS app store link should use app store domain: itunes.apple.com");
            } else if (!TextUtils.isEmpty(stickerPack.licenseAgreementWebsite) && !isValidWebsiteUrl(stickerPack.licenseAgreementWebsite)) {
                throw new IllegalStateException("Make sure to include http or https in url links, license agreement link is not strings valid url: " + stickerPack.licenseAgreementWebsite);
            } else if (!TextUtils.isEmpty(stickerPack.privacyPolicyWebsite) && !isValidWebsiteUrl(stickerPack.privacyPolicyWebsite)) {
                throw new IllegalStateException("Make sure to include http or https in url links, privacy policy link is not strings valid url: " + stickerPack.privacyPolicyWebsite);
            } else if (!TextUtils.isEmpty(stickerPack.publisherWebsite) && !isValidWebsiteUrl(stickerPack.publisherWebsite)) {
                throw new IllegalStateException("Make sure to include http or https in url links, publisher website link is not strings valid url: " + stickerPack.publisherWebsite);
            } else if (TextUtils.isEmpty(stickerPack.publisherEmail) || Patterns.EMAIL_ADDRESS.matcher(stickerPack.publisherEmail).matches()) {
                try {
                    byte[] fetchStickerAsset = StickerPackLoader.fetchStickerAsset(stickerPack.identifier, stickerPack.trayImageFile, context.getContentResolver());
                    if (((long) fetchStickerAsset.length) <= 409600) {
                        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(fetchStickerAsset, 0, fetchStickerAsset.length);
                        if (decodeByteArray.getHeight() <= 512) {
                            if (decodeByteArray.getHeight() >= 24) {
                                if (decodeByteArray.getWidth() > 512 || decodeByteArray.getWidth() < 24) {
                                    throw new IllegalStateException("tray image width should be between 24 and 512 pixels, current tray image width is " + decodeByteArray.getWidth() + ", tray image file: " + stickerPack.trayImageFile);
                                }
                                List<Sticker> stickers = stickerPack.getStickers();
                                if (stickers.size() < 3 || stickers.size() > 30) {
                                    throw new IllegalStateException("sticker pack sticker count should be between 3 to 30 inclusive, it currently has " + stickers.size() + ", sticker pack identifier:" + stickerPack.identifier);
                                }
                                for (Sticker validateSticker : stickers) {
                                    validateSticker(context, stickerPack.identifier, validateSticker);
                                }
                                return;
                            }
                        }
                        throw new IllegalStateException("tray image height should between 24 and 512 pixels, current tray image height is " + decodeByteArray.getHeight() + ", tray image file: " + stickerPack.trayImageFile);
                    }
                    throw new IllegalStateException("tray image should be less than 50 KB, tray image file: " + stickerPack.trayImageFile);
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot open tray image, " + stickerPack.trayImageFile, e);
                }
            } else {
                throw new IllegalStateException("publisher email does not seem valid, email is: " + stickerPack.publisherEmail);
            }
        } else {
            throw new IllegalStateException("sticker pack identifier cannot exceed 128 characters");
        }
    }

    private static void validateSticker(@NonNull Context context, @NonNull String str, @NonNull Sticker sticker) throws IllegalStateException {
        if (sticker.emojis.size() > 3) {
            throw new IllegalStateException("emoji count exceed limit, sticker pack identifier:" + str + ", filename:" + sticker.imageFileName);
        } else if (!TextUtils.isEmpty(sticker.imageFileName)) {
            validateStickerFile(context, str, sticker.imageFileName);
        } else {
            throw new IllegalStateException("no file path for sticker, sticker pack identifier:" + str);
        }
    }

    private static void validateStickerFile(@NonNull Context context, @NonNull String str, @NonNull String str2) throws IllegalStateException {
        try {
            if (((long) StickerPackLoader.fetchStickerAsset(str, str2, context.getContentResolver()).length) > 819200) {
                throw new IllegalStateException("sticker should be less than 100KB, sticker pack identifier:" + str + ", filename:" + str2);
            }
        } catch (IOException e) {
            throw new IllegalStateException("cannot open sticker file: sticker pack identifier:" + str + ", filename:" + str2, e);
        }
    }

    private static void checkStringValidity(@NonNull String str) {
        if (!str.matches("[\\w-.,'\\s]+")) {
            throw new IllegalStateException(str + " contains invalid characters, allowed characters are strings to z, A to Z, _ , ' - . and space character");
        } else if (str.contains("..")) {
            throw new IllegalStateException(str + " cannot contain ..");
        }
    }

    private static boolean isValidWebsiteUrl(String str) throws IllegalStateException {
        try {
            new URL(str);
            return URLUtil.isHttpUrl(str) || URLUtil.isHttpsUrl(str);
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + str + " is malformed");
            throw new IllegalStateException("url: " + str + " is malformed", e);
        }
    }

    private static boolean isURLInCorrectDomain(String str, String str2) throws IllegalStateException {
        try {
            return str2.equals(new URL(str).getHost());
        } catch (MalformedURLException unused) {
            Log.e("StickerPackValidator", "url: " + str + " is malformed");
            throw new IllegalStateException("url: " + str + " is malformed");
        }
    }
}

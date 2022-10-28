package com.moutamid.whatstool;

import static com.facebook.imageutils.HeifExifUtil.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.moutamid.whatstool.emotions.EmotionsActivity;
import com.moutamid.whatstool.instareshare.InstaReshareActivity;
import com.moutamid.whatstool.statussaver.StatusSaverMainActivity;
import com.moutamid.whatstool.textmagic.TextMagicActivity;
import com.moutamid.whatstool.whatsappsticker.StickerPack;
import com.moutamid.whatstool.whatsappsticker.StickerPackDetailsActivity;
import com.moutamid.whatstool.whatsappsticker.StickerPackListActivity;
import com.moutamid.whatstool.whatsappsticker.StickerPackLoader;
import com.moutamid.whatstool.whatsappsticker.StickerPackValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

public class MainActivity extends AppCompatActivity{
    ImageView aboutBtn;
    CardView blankMessge, instaShare, whatsWeb, textmagic, emotions, StatusSaver, texttoemoji, directChatBtn, makeStory, textRepeater, QrGenerat, QrScanner, captions, whatsShort, makeProfile;

    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    private ArrayList<ServerModel> serverLists;
    private CheckInternet connection;

    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;
    private ServerModel server;
    private newserver ns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aboutBtn = findViewById(R.id.aboutBtn);
        blankMessge = findViewById(R.id.blankMessgBtn);
        whatsWeb = findViewById(R.id.whatsWeb);
        directChatBtn = findViewById(R.id.directChatBtn);
        makeStory = findViewById(R.id.makeStory);
        textRepeater = findViewById(R.id.textRepeater);
        makeProfile = findViewById(R.id.makeProfile);
        QrGenerat = findViewById(R.id.QrGenerat);
        QrScanner = findViewById(R.id.QrScanner);
        captions = findViewById(R.id.captions);
        StatusSaver = findViewById(R.id.StatusSaver);
        texttoemoji = findViewById(R.id.texttoemoji);
        emotions = findViewById(R.id.emotions);
        textmagic = findViewById(R.id.textmagic);
        instaShare = findViewById(R.id.instaShare);

        preference = new SharedPreference(this);
        server = new ServerModel(
                "USA",
                "vpnbook-us1-tcp443.ovpn",
                "vpnbook",
                "79c8xza"
        );
        serverLists = getServerList();

//        new newserver() {
//            @Override
//            public void newServer(ServerModel ser) {
//                server = ser;
//                //updateCurrentServerIcon(server.getFlagUrl());
//
//                // Stop previous connection
//                if (vpnStart) {
//                    stopVpn();
//                }
//                prepareVpn();
//            }
//        };

        // Update current selected server icon
        //updateCurrentServerIcon(server.getFlagUrl());

        connection = new CheckInternet();

        try {
            isServiceRunning();
            VpnStatus.initLogCache(this.getCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (vpnStart) {
                confirmDisconnect();
            }else {
                prepareVpn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        blankMessge.setOnClickListener(v -> {
            startActivity(new Intent(this, BlankMessageActivity.class));
        });
        textmagic.setOnClickListener(v -> {
            startActivity(new Intent(this, TextMagicActivity.class));
        });
        texttoemoji.setOnClickListener(v -> {
            startActivity(new Intent(this, TextToEmojiActivity.class));
        });
        emotions.setOnClickListener(v -> {
            startActivity(new Intent(this, EmotionsActivity.class));
        });
        StatusSaver.setOnClickListener(v -> {
            startActivity(new Intent(this, StatusSaverMainActivity.class));
        });
        makeProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, MakeProfileActivity.class));
        });
        captions.setOnClickListener(v -> {
            startActivity(new Intent(this, CaptionsItemActivity.class));
        });
        textRepeater.setOnClickListener(v -> {
            startActivity(new Intent(this, TextRepeaterActivity.class));
        });
        QrGenerat.setOnClickListener(v -> {
            startActivity(new Intent(this, QRGeneratorActivity.class));
        });
        QrScanner.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });
        directChatBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, DirectChatActivity.class));
        });
        whatsWeb.setOnClickListener(v -> {
            startActivity(new Intent(this, WhatsWebActivity.class));
        });
        makeStory.setOnClickListener(v -> {
            startActivity(new Intent(this, MakeStoryActivity.class));
        });

        instaShare.setOnClickListener(v -> {
            startActivity(new Intent(this, InstaReshareActivity.class));
        });
        aboutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        findViewById(R.id.stickers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.progressDialog.show();
                Fresco.initialize(MainActivity.this);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.loadListAsyncTask = new LoadListAsyncTask(mainActivity);
                MainActivity.this.loadListAsyncTask.execute(new Void[0]);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);

        adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        loadIntersAd();

    }

    AdListener adListener = new AdListener() {
        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            super.onAdFailedToLoad(loadAdError);
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }
    };

    private void loadIntersAd() {
        InterstitialAd.load(this,getResources().getString(R.string.ad_interst_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });

                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }, 5000);
    }

    private ProgressDialog progressDialog;
    public LoadListAsyncTask loadListAsyncTask;

    static class LoadListAsyncTask extends AsyncTask<Void, Void, Pair<String, ArrayList<StickerPack>>> {
        private final WeakReference<MainActivity> contextWeakReference;

        LoadListAsyncTask(MainActivity mainActivity) {
            this.contextWeakReference = new WeakReference<>(mainActivity);
        }


        public Pair<String, ArrayList<StickerPack>> doInBackground(Void... voidArr) {
            try {
                Context context = (Context) this.contextWeakReference.get();
                if (context == null) {
                    return new Pair<>("could not fetch sticker packs", null);
                }
                ArrayList<StickerPack> fetchStickerPacks = StickerPackLoader.fetchStickerPacks(context);
                if (fetchStickerPacks.size() == 0) {
                    return new Pair<>("could not find any packs", null);
                }
                Iterator<StickerPack> it = fetchStickerPacks.iterator();
                while (it.hasNext()) {
                    StickerPackValidator.verifyStickerPackValidity(context, it.next());
                }
                return new Pair<>(null, fetchStickerPacks);
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                return new Pair<>(e.getMessage(), null);
            }
        }


        @Override
        public void onPostExecute(Pair<String, ArrayList<StickerPack>> pair) {
            MainActivity mainActivity = (MainActivity) this.contextWeakReference.get();
            if (mainActivity == null) {
                return;
            }
            if (pair.first != null) {
                mainActivity.showErrorMessage((String) pair.first);
            } else {
                mainActivity.showStickerPack((ArrayList) pair.second);
            }
        }
    }

    public void showStickerPack(ArrayList<StickerPack> arrayList) {
        if (arrayList.size() > 1) {
            this.progressDialog.dismiss();
            Intent intent = new Intent(this, StickerPackListActivity.class);
            intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, arrayList);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return;
        }
        this.progressDialog.dismiss();
        Intent intent2 = new Intent(this, StickerPackDetailsActivity.class);
        intent2.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
        intent2.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, arrayList.get(0));
        startActivity(intent2);
        overridePendingTransition(0, 0);
    }


    public void showErrorMessage(String str) {
        this.progressDialog.dismiss();
        Log.e("Main activity", "error fetching sticker packs, " + str);
        Toast.makeText(this, "" + str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show show disconnect confirm dialog
     */
    public void confirmDisconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close Connection");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList getServerList() {

        ArrayList<ServerModel> servers = new ArrayList<>();

        /*servers.add(new ServerModel("United States",
                "us.ovpn",
                "freeopenvpn",
                "416248023"
        ));*/
        servers.add(new ServerModel("USA",
                "vpnbook-us1-tcp443.ovpn",
                "vpnbook",
                "79c8xza"
        ));

        return servers;
    }

    /**
     * Prepare for vpn connect with required permission
     */
    private void prepareVpn() {
        try {
            if (!vpnStart) {
                if (getInternetStatus()) {
                    // Checking permission for network monitor
                    try {
                        Intent intent = VpnService.prepare(this);
                        if (intent != null) {
                            startActivityForResult(intent, 1);
                        } else startVpn();//have already permission
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Update confection status
                    status("connecting");
                } else {
                    // No internet connection available
                    showToast("you have no internet connection !!");
                }
            } else if (stopVpn()) {
                showToast("Disconnect Successfully");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    public boolean stopVpn() {
        try {
            vpnThread.stop();
            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Start the VPN
     */
    private void startVpn() {
        try {
            // .ovpn file
            InputStream conf = MainActivity.this.getResources().getAssets().open(server.getOvpn());
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();

            try {
                OpenVpnApi.startVpn(this, config, server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());
            } catch (Exception e){
                e.printStackTrace();
            }

            // Update log
            Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
            vpnStart = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setStatus(String connectionState) {
        if (connectionState!= null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    vpnStart = false;
                    vpnService.setDefaultStatus();
                    /*binding.gif.setVisibility(View.GONE);
                    binding.gif2.setVisibility(View.VISIBLE);*/
                    Toast.makeText(this, "Connect Now !!!", Toast.LENGTH_SHORT).show();
                    break;
                case "CONNECTED":
                    vpnStart = true;// it will use after restart this activity
                    status("connected");
                    /*binding.logTv.setText("Connected with VPN...");
                    binding.gif.setVisibility(View.VISIBLE);
                    binding.gif2.setVisibility(View.GONE);*/
                    break;
                case "WAIT":
                    Toast.makeText(this, "Waiting for Server Connection!!", Toast.LENGTH_SHORT).show();
                    break;
                case "AUTH":
                    Toast.makeText(this, "Server Authenticating!!", Toast.LENGTH_SHORT).show();
                    break;
                case "RECONNECTING":
                    status("connecting");
                    Toast.makeText(this, "Reconnecting...", Toast.LENGTH_SHORT).show();
                    break;
                case "NONETWORK":
                    Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
                    break;
            }

    }

    /**
     * Internet connection status.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(this);
    }

    /**
     * Get service status
     */
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }



    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    public void status(String status) {

        if (status.equals("connect")) {
            Toast.makeText(this, getResources().getString(R.string.connect), Toast.LENGTH_SHORT).show();
        } else if (status.equals("connecting")) {
            Toast.makeText(this, getResources().getString(R.string.connecting), Toast.LENGTH_SHORT).show();
        } else if (status.equals("connected")) {
            Toast.makeText(this, getResources().getString(R.string.disconnect), Toast.LENGTH_SHORT).show();
        } else if (status.equals("tryDifferentServer")) {
            Toast.makeText(this, "Try Different\nServer", Toast.LENGTH_SHORT).show();
        } else if (status.equals("loading")) {
            Toast.makeText(this, "Loading Server..", Toast.LENGTH_SHORT).show();
        } else if (status.equals("invalidDevice")) {
            Toast.makeText(this, "Invalid Device", Toast.LENGTH_SHORT).show();
        } else if (status.equals("authenticationCheck")) {
            Toast.makeText(this, "Authentication \n Checking...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Taking permission for network access
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK) {
                //Permission granted, start the VPN
                try {
                    startVpn();
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                showToast("Permission Deny !! ");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Receive broadcast message
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";
                //updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Show toast message
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));
            if (server == null) {
                server = preference.getServer();
            }
            super.onResume();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
            super.onPause();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Save current selected server on local shared preference
     */
    @Override
    public void onStop() {
        try {
            if (server != null) {
                preference.saveServer(server);
            }
            super.onStop();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
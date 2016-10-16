/*
 * Copyright (c) 2016. René Kruithof
 */

package com.example.renaigu.dctv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.renaigu.dctv.fragments.OneFragment;
import com.example.renaigu.dctv.fragments.ThreeFragment;
import com.example.renaigu.dctv.fragments.TwoFragment;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import java.util.ArrayList;
import java.util.List;

//import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
//    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;
    private int[] tabIcons = {
            R.drawable.ic_video_library_white_24dp,
            R.drawable.ic_radio_white_24dp,
            R.drawable.ic_menu_white_24dp
    };
    private VideoCastManager mCastManager;
//
   private MiniController mMini;
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private VideoCastConsumerImpl mCastConsumer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseCastManager.checkGooglePlayServices(this);
        CastConfiguration options = new CastConfiguration.Builder(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
                .enableAutoReconnect()
                .enableDebug()
                .enableLockScreen()
                .enableWifiReconnection()
                .enableNotification()
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
                .build();
        VideoCastManager.initialize(this, options);
        mCastManager = VideoCastManager.getInstance();
        setContentView(R.layout.activity_main);





        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//=        mCastContext = CastContext.getSharedInstance(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        mMini = (MiniController) findViewById(R.id.miniController);



        mCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onCastDeviceDetected(final MediaRouter.RouteInfo info) {
                if (!isFtuShown(MainActivity.this) && mIsHoneyCombOrAbove) {
                    setFtuShown(MainActivity.this);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaRouteMenuItem.isVisible()) {
                                showFtu();
                            }
                        }
                    }, 1000);
                }
            }
        };
    }
    public static final String FTU_SHOWN_KEY = "ftu_shown";
    public static boolean isFtuShown(Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getBoolean(FTU_SHOWN_KEY, false);
    }

    public static void setFtuShown(Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPref.edit().putBoolean(FTU_SHOWN_KEY, true).commit();
    }
    private void showFtu() {
        Menu menu = toolbar.getMenu();
        View view = menu.findItem(R.id.media_route_menu_item).getActionView();
        if (view != null && view instanceof MediaRouteButton) {
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(view))
                    .setContentTitle(R.string.touch_to_cast)
                    .build();
        }
    }



    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "Live Video");
        adapter.addFrag(new TwoFragment(), "Live Audio");
        adapter.addFrag(new ThreeFragment(), "Misc");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.browse, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }




    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        mCastManager.decrementUiCounter();
//
        mCastManager.removeMiniController(mMini);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        mCastManager = VideoCastManager.getInstance();
        mCastManager.incrementUiCounter();
        mCastManager.addMiniController(mMini);
    }}

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

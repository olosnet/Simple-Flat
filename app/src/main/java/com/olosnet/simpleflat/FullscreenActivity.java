package com.olosnet.simpleflat;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.olosnet.simpleflat.buses.ConfigsBus;
import com.olosnet.simpleflat.buses.ProfilesBus;
import com.olosnet.simpleflat.database.ConfigsManager;
import com.olosnet.simpleflat.database.ProfilesManager;
import com.olosnet.simpleflat.database.SimpleFlatDatabase;
import com.olosnet.simpleflat.databinding.ActivityFullscreenBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private static final float DEFAULT_BRIGHTNESS = 1.0f;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int MAX_COLOR = 255;
    private static float _currentBrightness;
    private static int _currentR;
    private static int _currentG;
    private static int _currentB;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private boolean _settingsFragmentVisibile = false;
    private boolean mVisible;
    private View mContentView;
    private final List<Disposable> subs = new ArrayList<>();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI element
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private ActivityFullscreenBinding binding;
    private final Runnable mHideRunnable = this::hide;


    private final View.OnClickListener mSettingsOnClickListener = view -> showSettingsFragment();

    private void showSettingsFragment() {
        binding.settingsFragment.setVisibility(View.VISIBLE);
        binding.settingFloating.setVisibility(View.GONE);
        _settingsFragmentVisibile = true;
    }

    private void hideSettingsFragment() {
        binding.settingsFragment.setVisibility(View.GONE);
        binding.settingFloating.setVisibility(View.VISIBLE);
        _settingsFragmentVisibile = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        // Database
        SimpleFlatDatabase database = SimpleFlatDatabase.getInstance(getApplicationContext());
        ProfilesManager.init(database);
        ConfigsManager.init(database);
        ProfilesBus.loadRequestSubject().onNext(1); // Load existent profiles
        ConfigsBus.readAllRequestSubject().onNext(true); // Load configs

        // Disable screen timeout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view -> toggle());

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.settingFloating.setOnClickListener(mSettingsOnClickListener);

        // Bus observables
        subs.add(ConfigsBus.rSubject().subscribe(this::setR));
        subs.add(ConfigsBus.gSubject().subscribe(this::setG));
        subs.add(ConfigsBus.bSubject().subscribe(this::setB));
        subs.add(ConfigsBus.brightnessSubject().subscribe(this::setBrightness));

        if (savedInstanceState != null) {
            int R = savedInstanceState.getInt("currentR", MAX_COLOR);
            int G = savedInstanceState.getInt("currentG", MAX_COLOR);
            int B = savedInstanceState.getInt("currentG", MAX_COLOR);
            float brightness = savedInstanceState.getFloat("currentBrightness", DEFAULT_BRIGHTNESS);

            // Review
            ConfigsBus.rSubject().onNext(R);
            ConfigsBus.gSubject().onNext(G);
            ConfigsBus.bSubject().onNext(B);
            ConfigsBus.brightnessSubject().onNext(brightness);
            ConfigsBus.readAllSubject().onNext(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        if (_settingsFragmentVisibile)
            hideSettingsFragment(); // Ripristino allo stato originario

        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        updateMainPadding(false);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {

            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        updateMainPadding(true);

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("currentR", _currentR);
        outState.putInt("currentG", _currentG);
        outState.putInt("currentB", _currentB);
        outState.putFloat("currentBrightness", _currentBrightness);
        super.onSaveInstanceState(outState);
    }

    private void updateMainPadding(boolean add) {
        int paddingBottom = 0;

        if (add) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                paddingBottom = resources.getDimensionPixelSize(resourceId);
            }
        }
        binding.fullScreenLayout.setPadding(0, 0, 0, paddingBottom);
    }

    private void setR(Integer value) {
        _currentR = value;
        binding.fullscreenContent.setBackgroundColor(
                Color.argb(255, _currentR, _currentB, _currentG));
    }

    private void setG(Integer value) {
        _currentG = value;
        binding.fullscreenContent.setBackgroundColor(
                Color.argb(255, _currentR, _currentB, _currentG));
    }

    private void setB(Integer value) {
        _currentB = value;
        binding.fullscreenContent.setBackgroundColor(
                Color.argb(255, _currentR, _currentB, _currentG));
    }

    private void setBrightness(Float value) {
        _currentBrightness = value;
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = value;
        window.setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        for (Disposable element : subs)
            element.dispose();

        super.onDestroy();
    }
}
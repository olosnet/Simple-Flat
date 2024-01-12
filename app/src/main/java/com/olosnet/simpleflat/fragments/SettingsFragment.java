package com.olosnet.simpleflat.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.olosnet.simpleflat.R;
import com.olosnet.simpleflat.buses.ConfigsBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class SettingsFragment extends Fragment {
    private final List<Disposable> subs = new ArrayList<>();

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private SeekBar r_seek, g_seek, b_seek, brightness_seek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView r_value = view.findViewById(R.id.r_value);
        TextView g_value = view.findViewById(R.id.g_value);
        TextView b_value = view.findViewById(R.id.b_value);
        TextView brightness_value = view.findViewById(R.id.brightness_value);

        r_seek = view.findViewById(R.id.r_value_seek);
        g_seek = view.findViewById(R.id.g_value_seek);
        b_seek = view.findViewById(R.id.b_value_seek);
        brightness_seek = view.findViewById(R.id.brightness_value_seek);

        r_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ConfigsBus.writeRedRequest().onNext(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        g_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ConfigsBus.writeGreenRequest().onNext(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        b_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ConfigsBus.writeBlueRequest().onNext(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brightness_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ConfigsBus.writeBrightnessRequest().onNext((float)progress/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        subs.add(ConfigsBus.onRedUpdated().subscribe(value -> r_value.setText(value.toString())));
        subs.add(ConfigsBus.onGreenUpdated().subscribe(value -> g_value.setText(value.toString())));
        subs.add(ConfigsBus.onBlueUpdated().subscribe(value -> b_value.setText(value.toString())));
        subs.add(ConfigsBus.onBrightnessUpdated().subscribe(value -> brightness_value.setText(value.toString())));

        setSeeksProgress(); //First time set
        subs.add(ConfigsBus.onReadAll().subscribe(value -> setSeeksProgress()));

        return view;
    }

    void setSeeksProgress() {
        int current_r = ConfigsBus.onRedUpdated().getValue();
        int current_g = ConfigsBus.onGreenUpdated().getValue();
        int current_b = ConfigsBus.onBlueUpdated().getValue();
        int current_brightness = (int)(ConfigsBus.onBrightnessUpdated().getValue()*100);

        r_seek.setProgress(current_r);
        g_seek.setProgress(current_g);
        b_seek.setProgress(current_b);
        brightness_seek.setProgress(current_brightness);
    }

    @Override
    public void onDestroy() {
        for (Disposable element : subs)
            element.dispose();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        setSeeksProgress();
        super.onResume();
    }

    @Override
    public void onStart() {
        setSeeksProgress();
        super.onStart();
    }

}
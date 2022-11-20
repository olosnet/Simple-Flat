package com.olosnet.simpleflat.database;

import android.os.Handler;
import android.os.Looper;

import com.olosnet.simpleflat.buses.ConfigsBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigsManager {

    private static ConfigsManager manager;
    private static SimpleFlatDatabase database;

    public static final String R_KEY = "CURRENT_R";
    public static final String G_KEY = "CURRENT_G";
    public static final String B_KEY = "CURRENT_B";
    public static final String BRIGHTNESS_KEY = "CURRENT_BRIGHTNESS";

    public static ConfigsManager init(SimpleFlatDatabase database) {
        if(ConfigsManager.manager  == null) {
            ConfigsManager.manager = new ConfigsManager();
            ConfigsManager.database = database;
            setManager();
        }

        return ConfigsManager.manager;
    }

    private static void setManager() {
        ConfigsBus.writeRedSubject().subscribe(value -> createConfig(R_KEY, value.toString()));
        ConfigsBus.writeGreenSubject().subscribe(value -> createConfig(G_KEY, value.toString()));
        ConfigsBus.writeBlueSubject().subscribe(value -> createConfig(B_KEY, value.toString()));
        ConfigsBus.writeBrightnessSubject().subscribe(value -> createConfig(BRIGHTNESS_KEY, value.toString()));
        ConfigsBus.readAllRequestSubject().subscribe(value -> readAll());
    }

    private static void createConfig(String key, String value) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        ConfigsModel config = new ConfigsModel();
        config.setCkey(key);
        config.setValue(value);

        executor.execute(() -> {
            database.configsDao().createConfigEntry(config);
            handler.post(() -> nextValue(config));
        });
    }

    private static void readAll() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<ConfigsModel> configs = database.configsDao().getAll();
            handler.post(() -> {
                for (ConfigsModel element : configs) {
                    nextValue(element);
                }
                ConfigsBus.readAllSubject().onNext(true);
            });
        });
    }

    private static void nextValue(ConfigsModel model) {

        switch (model.getCkey()) {
            case R_KEY:
                Integer CR = Integer.parseInt(model.getValue());
                ConfigsBus.rSubject().onNext(CR);
                break;
            case G_KEY:
                Integer CG = Integer.parseInt(model.getValue());
                ConfigsBus.gSubject().onNext(CG);
                break;
            case B_KEY:
                Integer CB = Integer.parseInt(model.getValue());
                ConfigsBus.bSubject().onNext(CB);
                break;
            case BRIGHTNESS_KEY:
                Float CBR = Float.parseFloat(model.getValue());
                ConfigsBus.brightnessSubject().onNext(CBR);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + model.getCkey());
        }
    }
}

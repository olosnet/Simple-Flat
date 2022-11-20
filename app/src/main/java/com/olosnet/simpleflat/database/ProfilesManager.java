package com.olosnet.simpleflat.database;

import android.os.Handler;
import android.os.Looper;

import com.olosnet.simpleflat.buses.ProfilesBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfilesManager {

    private static ProfilesManager manager;
    private static SimpleFlatDatabase database;

    public static ProfilesManager init(SimpleFlatDatabase database) {
        if(ProfilesManager.manager == null) {
            ProfilesManager.manager = new ProfilesManager();
            ProfilesManager.database = database;
            setManager();
        }

        return ProfilesManager.manager;
    }

    private static void setManager() {
        ProfilesBus.createRequest().subscribe(ProfilesManager::createProfile);
        ProfilesBus.deleteRequest().subscribe(ProfilesManager::deleteProfile);
        ProfilesBus.loadRequest().subscribe(value -> loadProfiles());
        ProfilesBus.saveRequest().subscribe(ProfilesManager::saveProfile);
    }

    private static void deleteProfile(Long profile_id)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            database.profilesDao().deleteProfileByID(profile_id);
            handler.post(() -> ProfilesBus.onDeleted().onNext(profile_id));
        });
    }

    private static void createProfile(ProfilesModel model) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            database.profilesDao().createProfileEntry(model);
            handler.post(() -> ProfilesBus.onCreated().onNext(model));
        });
    }

    private static void loadProfiles() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<ProfilesModel> newProfiles = database.profilesDao().getAll();
            handler.post(() -> ProfilesBus.onLoaded().onNext(newProfiles));
        });
    }

    private static void saveProfile(ProfilesModel model) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            database.profilesDao().updateProfileEntry(model);
            handler.post(() -> ProfilesBus.onSaved().onNext(model));
        });
    }
}

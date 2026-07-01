package com.olosnet.simpleflat.database;

import android.os.Handler;
import android.os.Looper;

import com.olosnet.simpleflat.buses.ProfilesBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfilesManager {

    private static ProfilesManager manager;
    private static SimpleFlatDatabase database;
    private static final List<Disposable> subs = new ArrayList<>();

    public static ProfilesManager init(SimpleFlatDatabase database) {
        if (ProfilesManager.manager == null) {
            ProfilesManager.manager = new ProfilesManager();
            ProfilesManager.database = database;
            setManager();
        }

        return ProfilesManager.manager;
    }

    private static void setManager() {
        subs.add(ProfilesBus.createRequest().subscribe(ProfilesManager::createProfile));
        subs.add(ProfilesBus.deleteRequest().subscribe(ProfilesManager::deleteProfile));
        subs.add(ProfilesBus.loadRequest().subscribe(value -> loadProfiles()));
        subs.add(ProfilesBus.saveRequest().subscribe(ProfilesManager::saveProfile));
        subs.add(ProfilesBus.importRequest().subscribe(ProfilesManager::importProfiles));
    }

    private static void deleteProfile(Long profile_id) {
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

    private static void importProfiles(ImportProfilesType iprofiles) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (iprofiles.remove_old)
                database.profilesDao().deleteAllProfiles();

            // Workaround, find different solution
            for (ProfilesModel model : iprofiles.models) {
                model.setId(null);
                database.profilesDao().createProfileEntry(model);
            }

            handler.post(() -> ProfilesBus.onImported().onNext(iprofiles));
        });
    }
}

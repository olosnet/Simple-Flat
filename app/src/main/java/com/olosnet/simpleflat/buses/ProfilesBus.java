package com.olosnet.simpleflat.buses;

import com.olosnet.simpleflat.database.ImportProfilesType;
import com.olosnet.simpleflat.database.ProfilesModel;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;


public class ProfilesBus {

    // Create
    private static final PublishSubject<ProfilesModel> createProfileRequestSubject
            = PublishSubject.create();
    private static final PublishSubject<ProfilesModel> onCreatedProfileSubject = PublishSubject.create();

    public static PublishSubject<ProfilesModel> createRequest() {
        return createProfileRequestSubject;
    }

    public static PublishSubject<ProfilesModel> onCreated() {
        return onCreatedProfileSubject;
    }

    // Delete
    private static final PublishSubject<Long> deleteProfileRequestSubject
            = PublishSubject.create();
    private static final PublishSubject<Long> onDeletedProfileSubject = PublishSubject.create();

    public static PublishSubject<Long> deleteRequest() {
        return deleteProfileRequestSubject;
    }

    public static PublishSubject<Long> onDeleted() {
        return onDeletedProfileSubject;
    }

    // Save
    private static final PublishSubject<ProfilesModel> saveProfileRequestSubject = PublishSubject.create();
    private static final PublishSubject<ProfilesModel> onProfileSavedSubject = PublishSubject.create();

    public static PublishSubject<ProfilesModel> saveRequest() {
        return saveProfileRequestSubject;
    }

    public static PublishSubject<ProfilesModel> onSaved() {
        return onProfileSavedSubject;
    }

    // Load
    public static PublishSubject<Boolean> loadProfilesRequestSubject = PublishSubject.create();
    public static BehaviorSubject<List<ProfilesModel>> onLoadedSubject =
            BehaviorSubject.createDefault(new ArrayList<>());

    public static PublishSubject<Boolean> loadRequest() {
        return loadProfilesRequestSubject;
    }

    public static BehaviorSubject<List<ProfilesModel>> onLoaded() {
        return onLoadedSubject;
    }


    // Import
    private static final PublishSubject<ImportProfilesType> importProfilesRequestSubject = PublishSubject.create();
    private static final PublishSubject<ImportProfilesType> onImportComplete = PublishSubject.create();

    public static PublishSubject<ImportProfilesType> importRequest() {
        return importProfilesRequestSubject;
    }

    public static PublishSubject<ImportProfilesType> onImported() {
        return onImportComplete;
    }
}

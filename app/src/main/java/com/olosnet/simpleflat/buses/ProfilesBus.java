package com.olosnet.simpleflat.buses;
import com.olosnet.simpleflat.database.ProfilesModel;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;


public class ProfilesBus {
    private static final PublishSubject<ProfilesModel> createProfileRequestSubject
            =  PublishSubject.create();
    private static final PublishSubject<Long> deleteProfileRequestSubject
            = PublishSubject.create();

    private static final PublishSubject<Long> onDeleteProfileSubject = PublishSubject.create();
    private static final PublishSubject<ProfilesModel> onCreateProfileSubject = PublishSubject.create();

    public static PublishSubject loadProfilesRequestSubject = PublishSubject.create();
    public static BehaviorSubject<List<ProfilesModel>> onLoadProfilesSubject =
            BehaviorSubject.createDefault(new ArrayList<>());


    public static PublishSubject<ProfilesModel> createRequestSubject() { return createProfileRequestSubject;}
    public static PublishSubject<ProfilesModel> onCreateSubject() {return onCreateProfileSubject; }

    public static PublishSubject<Long> deleteRequestSubject() { return deleteProfileRequestSubject;}
    public static PublishSubject<Long> onDeleteSubject() { return onDeleteProfileSubject; }

    public static PublishSubject loadRequestSubject() {return loadProfilesRequestSubject;}
    public static BehaviorSubject<List<ProfilesModel>> onLoadSubject() {return onLoadProfilesSubject;}
}

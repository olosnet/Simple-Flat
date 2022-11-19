package com.olosnet.simpleflat.buses;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class ProfilesBus {

    private static final PublishSubject<Integer> onDeleteProfileSubject = PublishSubject.create();
    private static final PublishSubject<Integer> onCreateProfileSubject = PublishSubject.create();

    public static PublishSubject<Integer> onDeleteSubject() { return onDeleteProfileSubject; }
    public static PublishSubject<Integer> getOnCreateSubject() {return onCreateProfileSubject; }
}

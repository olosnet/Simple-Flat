package com.olosnet.simpleflat.buses;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public final class ConfigsBus {

    private static final PublishSubject<Integer> wRRequestSubject = PublishSubject.create();
    private static final PublishSubject<Integer> wGRequestSubject = PublishSubject.create();
    private static final PublishSubject<Integer> wBRequestSubject = PublishSubject.create();
    private static final PublishSubject<Float> wBrightnessRequestSubject = PublishSubject.create();
    private static final PublishSubject <Boolean> readAllSubject = PublishSubject.create();
    private static final PublishSubject <Boolean> readAllRequestSubject = PublishSubject.create();

    private static final BehaviorSubject<Integer> rSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Integer> gSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Integer> bSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Float> brightnessSubject = BehaviorSubject.createDefault(1.0f);

    public static PublishSubject<Integer> writeRedSubject() {return wRRequestSubject;}
    public static BehaviorSubject<Integer> rSubject() {
        return rSubject;
    }

    public static PublishSubject<Integer> writeGreenSubject() {return wGRequestSubject;}
    public static BehaviorSubject<Integer> gSubject() {
        return gSubject;
    }

    public static PublishSubject<Integer> writeBlueSubject() {return wBRequestSubject;}
    public static BehaviorSubject<Integer> bSubject() {return bSubject;}

    public static PublishSubject<Float> writeBrightnessSubject() {return wBrightnessRequestSubject;}
    public static BehaviorSubject<Float> brightnessSubject() { return brightnessSubject; }

    public static PublishSubject<Boolean> readAllRequestSubject() { return readAllRequestSubject; }
    public static PublishSubject readAllSubject() { return readAllSubject;}
}

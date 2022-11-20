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

    public static PublishSubject<Integer> writeRedRequest() {return wRRequestSubject;}
    public static BehaviorSubject<Integer> onRedUpdated() {
        return rSubject;
    }

    public static PublishSubject<Integer> writeGreenRequest() {return wGRequestSubject;}
    public static BehaviorSubject<Integer> onGreenUpdated() {
        return gSubject;
    }

    public static PublishSubject<Integer> writeBlueRequest() {return wBRequestSubject;}
    public static BehaviorSubject<Integer> onBlueUpdated() {return bSubject;}

    public static PublishSubject<Float> writeBrightnessRequest() {return wBrightnessRequestSubject;}
    public static BehaviorSubject<Float> onBrightnessUpdated() { return brightnessSubject; }

    public static PublishSubject<Boolean> readAllRequest() { return readAllRequestSubject; }
    public static PublishSubject<Boolean> onReadAll() { return readAllSubject;}
}

package com.olosnet.simpleflat.buses;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public final class SimpleFlatBus {

    private static final BehaviorSubject<Integer> rSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Integer> gSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Integer> bSubject = BehaviorSubject.createDefault(255);
    private static final BehaviorSubject<Float> brightnessSubject = BehaviorSubject.createDefault(1.0f);
    private static final PublishSubject <Boolean> uSeekSubject = PublishSubject.create();

    public static BehaviorSubject<Integer> rSubject() {
        return rSubject;
    }

    public static BehaviorSubject<Integer> gSubject() {
        return gSubject;
    }

    public static BehaviorSubject<Integer> bSubject() {
        return bSubject;
    }

    public static BehaviorSubject<Float> brightnessSubject() { return brightnessSubject; }

    public static PublishSubject updateSeekSubject() { return uSeekSubject;}

}

package rip.alpha.libraries.timer;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Duration;

public abstract class Timer {

    @Getter(AccessLevel.PROTECTED)
    private transient boolean unreferenced;

    @Getter
    private boolean paused = false;
    private long endTimeStamp, pauseRemainingTime;

    public Timer(long millis) {
        this.endTimeStamp = System.currentTimeMillis() + millis;
    }

    public Timer(Duration duration) {
        this(duration.toMillis());
    }

    public void setRemainingMillis(long millis) {
        this.endTimeStamp = System.currentTimeMillis() + millis;
    }

    public void setExpired() {
        this.setRemainingMillis(0);
    }

    public long getRemainingMillis() {
        if (this.isPaused()) {
            return this.pauseRemainingTime;
        }
        return this.endTimeStamp - System.currentTimeMillis();
    }

    public boolean isDone() {
        return this.getRemainingMillis() <= 0;
    }

    public void reference() {
        this.unreferenced = false;
        TimerManager.getInstance().addTimer(this);
        this.onApply();
    }

    public void dereference() {
        this.unreferenced = true;
    }

    public void pause() {
        if (this.paused) {
            return;
        }
        this.pauseRemainingTime = this.getRemainingMillis();
        this.onPause();
        this.paused = true;
        this.dereference();
    }

    public void unpause() {
        if (!this.paused) {
            return;
        }
        this.endTimeStamp = System.currentTimeMillis() + this.pauseRemainingTime;
        this.onUnpause();
        this.paused = false;
        this.unreferenced = false;
        TimerManager.getInstance().addTimer(this);
    }

    public abstract void onPause();

    public abstract void onUnpause();

    public abstract void onExpire();

    public abstract void onTick();

    public abstract void onApply();

}

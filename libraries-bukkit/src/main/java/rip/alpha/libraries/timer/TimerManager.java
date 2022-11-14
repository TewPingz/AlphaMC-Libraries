package rip.alpha.libraries.timer;

import lombok.Getter;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.LibrariesConfig;
import rip.alpha.libraries.util.task.TaskUtil;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class TimerManager {

    @Getter
    private static final TimerManager instance = new TimerManager();

    private final LinkedList<Timer> timers = new LinkedList<>();
    private final Deque<Timer> addQueue = new ConcurrentLinkedDeque<>();

    private TimerManager() {
        LibrariesConfig config = Libraries.getConfig();
        TaskUtil.scheduleAtFixedRateOnPool(new TimerTask(), 0, config.getTimerTaskTickMillis(), TimeUnit.MILLISECONDS);
    }

    public void addTimer(Timer timer) {
        this.addQueue.add(timer);
    }

    protected void tickTimers() {
        while (!this.addQueue.isEmpty()) {
            this.timers.add(this.addQueue.poll());
        }
        this.timers.removeIf(this::checkAndExpire);
    }

    private boolean checkAndExpire(Timer timer) {
        if (timer.isPaused() || timer.isUnreferenced()) {
            return true;
        }
        if (timer.isDone()) {
            timer.onExpire();
            return true;
        }
        timer.onTick();
        return false;
    }
}

package rip.alpha.libraries.timer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerTask implements Runnable {
    @Override
    public void run() {
        TimerManager.getInstance().tickTimers();
    }
}

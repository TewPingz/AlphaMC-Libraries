package rip.alpha.libraries.util.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.alpha.libraries.LibrariesPlugin;

public abstract class AlphaRunnable {

    public abstract void run(BukkitRunnable runnable);

    private final BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            AlphaRunnable.this.run(AlphaRunnable.this.runnable);
        }
    };

    public BukkitTask runTask() {
        return this.runnable.runTask(LibrariesPlugin.getInstance().getBukkitPlugin());
    }

    public BukkitTask runTaskAsynchronously() {
        return this.runnable.runTaskAsynchronously(LibrariesPlugin.getInstance().getBukkitPlugin());
    }

    public BukkitTask runTaskLater(long delay) {
        return this.runnable.runTaskLater(LibrariesPlugin.getInstance().getBukkitPlugin(), delay);
    }

    public BukkitTask runTaskLaterAsynchronously(long delay) {
        return this.runnable.runTaskLaterAsynchronously(LibrariesPlugin.getInstance().getBukkitPlugin(), delay);
    }

    public BukkitTask runTaskTimer(long delay, long period) {
        return this.runnable.runTaskTimer(LibrariesPlugin.getInstance().getBukkitPlugin(), delay, period);
    }

    public BukkitTask runTaskTimerAsynchronously(long delay, long period) {
        return this.runnable.runTaskTimerAsynchronously(LibrariesPlugin.getInstance().getBukkitPlugin(), delay, period);
    }

}

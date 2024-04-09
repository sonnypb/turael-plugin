package com.turaelcounter;

import com.turaelcounter.TuraelCounterConfig;
import com.turaelcounter.TuraelCounterPlugin;
import net.runelite.client.eventbus.Subscribe;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import java.time.Duration;
import java.time.Instant;

import javax.inject.Inject;
import java.util.Timer;

@Slf4j
@Getter
public class TimerHandler {

    public enum TimerState
    {
        RUNNING,
        PAUSED,
        STOPPED
    }

//    private long duration;
    private TimerState state;
    private Duration totalDuration;
    private Instant startTime;
    private Instant pauseStartTime;

    private final TuraelCounterConfig config;
    private final TuraelCounterPlugin plugin;


    @Inject
    protected  TimerHandler(TuraelCounterPlugin plugin, TuraelCounterConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        this.totalDuration = Duration.ZERO;
        this.state = TimerState.STOPPED;
    }

    @Subscribe
    protected void timerStarted()
    {
        if (this.state == TimerState.STOPPED)
            this.startTime = Instant.now();

        else if (this.state == TimerState.PAUSED)
        {
//          calculate the paused time and update the start time
            Instant now = Instant.now();
            Duration pausedDuration = Duration.between(this.pauseStartTime, now);

        }
            //get duration from stopped timer
//            this.totalDuration = this.config.turaelDuration();

        this.state = TimerState.RUNNING;
        this.config.turaelTimerState(this.state);

        log.info("Timer state set to: " + this.state);
    }

    @Subscribe
    protected void timerPaused()
    {
        this.state = TimerState.PAUSED;
        this.pauseStartTime = Instant.now();
        this.config.turaelTimerState(this.state);
        log.info("Timer state set to: " + this.state);
    }

    @Subscribe
    protected void timerStopped()
    {
        this.state = TimerState.STOPPED;
        this.config.turaelTimerState(this.state);

        //calculate the final duration
        Instant now = Instant.now();
        this.totalDuration = this.totalDuration.plus(Duration.between(this.startTime, now));
        log.info("Total duration of timer class: " + totalDuration);

        this.config.turaelDuration(this.totalDuration);
        log.info("Timer state set to: " + this.state);
    }

    public Duration getDuration()
    {
        return this.totalDuration;
    }

    public void timerReset()
    {
        this.state = TimerState.STOPPED;
        this.config.turaelTimerState(this.state);
        this.totalDuration = Duration.ZERO;
        this.config.turaelDuration(this.totalDuration);
    }

}

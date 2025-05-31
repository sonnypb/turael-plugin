package com.example;

import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SurgePotionTimer
{
    private Timer timer;

    @Getter
    private SurgePotionTimerState state;

    SurgePotionTimer()
    {
        timer = null;
        state = SurgePotionTimerState.IDLE;
    }

    public void start() {
        timer = new Timer();

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                state = SurgePotionTimerState.EXPIRED;
            }
        }, 60 * 1000);

        state = SurgePotionTimerState.ACTIVE;
    }

    public void stop() {
        timer = null;
        state = SurgePotionTimerState.IDLE;
    }
}
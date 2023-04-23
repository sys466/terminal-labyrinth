package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimerSystem {

    private final long START_TIME = System.currentTimeMillis() / 1000;

    public long getMinutesLeft() {
        return TerminalLabyrinthApp.TIME_LIMIT_MINUTES - 1 - (System.currentTimeMillis() / 1000 - START_TIME) / 60;
    }

    public long getSecondsLeft() {
        return 60 - (System.currentTimeMillis() / 1000 - START_TIME) % 60;
    }

    public boolean isReachedTimeLimit() {
        return getMinutesLeft() < 0;
    }
}

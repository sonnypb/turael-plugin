package com.bawaveinfo;

public class WaveInfo
{
    private final int runners;
    private final int fighters;
    private final int rangers;
    private final int healers;

    private final int healerReserves;
    private final int fighterReserves;
    private final int rangerReserves;
    private final int runnerReserves;

    public WaveInfo(
            int runners,
            int fighters,
            int rangers,
            int healers,
            int healerReserves,
            int fighterReserves,
            int rangerReserves,
            int runnerReserves
    )
    {
        this.runners = runners;
        this.fighters = fighters;
        this.rangers = rangers;
        this.healers = healers;

        this.healerReserves = healerReserves;
        this.fighterReserves = fighterReserves;
        this.rangerReserves = rangerReserves;
        this.runnerReserves = runnerReserves;
    }

    public int runners() { return runners; }
    public int fighters() { return fighters; }
    public int rangers() { return rangers; }
    public int healers() { return healers; }

    public int healerReserves() { return healerReserves; }
    public int fighterReserves() { return fighterReserves; }
    public int rangerReserves() { return rangerReserves; }
    public int runnerReserves() { return runnerReserves; }
}

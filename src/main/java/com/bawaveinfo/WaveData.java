package com.bawaveinfo;


import java.util.Map;

public class WaveData {
    private static final Map<Integer, WaveInfo> WAVES = Map.of(
            1, new WaveInfo(2, 2, 2, 2, 0, 2,2,0),
            2, new WaveInfo(2, 2, 3, 2, 1, 3,1,1),
            3, new WaveInfo(2, 5, 3, 2, 1, 0,3,2),
            4, new WaveInfo(3, 5, 3, 3, 1, 1,3,1),
            5, new WaveInfo(4, 3, 3, 4, 1, 3,1,1),
            6, new WaveInfo(4, 5, 5, 4, 2, 1,2,2),
            7, new WaveInfo(5, 5, 6, 4, 3, 2,1,1),
            8, new WaveInfo(5, 7, 5, 5, 2, 0,3,2),
            9, new WaveInfo(5, 6, 7, 6, 2, 2,1,4),
            10, new WaveInfo(5, 5, 6, 4, 3, 2,1,1)
    );

    public static WaveInfo get(int wave)
    {
        return WAVES.get(wave);
    }
}

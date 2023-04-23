package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.map.Map;
import com.github.sys466.terminal.labyrinth.type.ElementChar;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MapSystem {

    private final char[][] MAP_DATA;

    static {

        MAP_DATA = new char[Map.MAP_HEIGHT][Map.MAP_WIDTH];

        List<String> maps = new ArrayList<>(Map.MAP_NUMBER);

        maps.add(Map.LEVEL_1_MAP);
        maps.add(Map.LEVEL_2_MAP);
        maps.add(Map.LEVEL_3_MAP);
        maps.add(Map.LEVEL_4_MAP);
        maps.add(Map.LEVEL_5_MAP);
        maps.add(Map.DEBUG_MAP);

        int index = TerminalLabyrinthApp.ENABLE_DEBUG_MAP
                ? maps.size() - 1
                : TerminalLabyrinthApp.random.nextInt(maps.size() - 1);

        initMapData(maps.get(index));
        placeExit();
    }

    public char getCharAt(int positionY, int positionX) {
        return MAP_DATA[positionY][positionX];
    }

    public char getCharAt(double positionY, double positionX) {
        return getCharAt((int) positionY, (int) positionX);
    }

    public boolean isUserReachedExit() {
        int positionY = (int) UserSystem.getPositionY();
        int positionX = (int) UserSystem.getPositionX();
        return getCharAt(positionY + 1, positionX) == ElementChar.EXIT
                || getCharAt(positionY - 1, positionX) == ElementChar.EXIT
                || getCharAt(positionY, positionX + 1) == ElementChar.EXIT
                || getCharAt(positionY, positionX - 1) == ElementChar.EXIT;
    }

    private void initMapData(String map) {
        int levelMapDataIndex = 0;
        for (int h = 0; h < Map.MAP_HEIGHT; h++) {
            for (int w = 0; w < Map.MAP_WIDTH; w++) {
                MAP_DATA[h][w] = map.charAt(levelMapDataIndex);
                levelMapDataIndex++;
            }
        }
    }

    private void placeExit() {
        int exitPositionY;
        int exitPositionX;
        while (true) {
            do {
                exitPositionY = TerminalLabyrinthApp.random.nextInt(Map.MAP_HEIGHT);
                exitPositionX = TerminalLabyrinthApp.random.nextInt(Map.MAP_WIDTH);
            } while (MAP_DATA[exitPositionY][exitPositionX] != ElementChar.WALL);
            if (exitPositionY + 1 < Map.MAP_HEIGHT && MAP_DATA[exitPositionY + 1][exitPositionX] == ElementChar.EMPTY
                    || exitPositionY - 1 >= 0 && MAP_DATA[exitPositionY - 1][exitPositionX] == ElementChar.EMPTY
                    || exitPositionX + 1 < Map.MAP_WIDTH && MAP_DATA[exitPositionY][exitPositionX + 1] == ElementChar.EMPTY
                    || exitPositionX - 1 >= 0 && MAP_DATA[exitPositionY][exitPositionX - 1] == ElementChar.EMPTY) {
                MAP_DATA[exitPositionY][exitPositionX] = ElementChar.EXIT;
                break;
            }
        }
    }
}

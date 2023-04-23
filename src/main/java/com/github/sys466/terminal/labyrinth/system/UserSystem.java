package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.map.Map;
import com.github.sys466.terminal.labyrinth.type.ElementChar;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserSystem {

    public final double VIEW_DISTANCE       = 16.0;
    public final double FOV                 = Math.PI / 4;
    public final double MAX_STAMINA         = 100.0;
    public final double USER_SIZE           = 0.2;

    private final double SPEED              = 0.04;
    private final double STAMINA_INCR_RATIO = 0.4;
    private final double STAMINA_DECR_RATIO = 0.8;

    private double positionY;
    private double positionX;
    private double viewAngle;
    private double stamina;

    static {
        do {
            positionY = TerminalLabyrinthApp.random.nextInt(Map.MAP_HEIGHT - 1) + 1;
            positionX = TerminalLabyrinthApp.random.nextInt(Map.MAP_WIDTH - 1) + 1;
        } while (MapSystem.getCharAt(positionY, positionX) != ElementChar.EMPTY);
        positionY += 0.5;
        positionX += 0.5;

        viewAngle = 0;
        stamina = MAX_STAMINA;
    }

    public void changePosition(boolean isNegative, boolean isRunning, boolean isSidewalk) {

        final double NEW_POSITION_Y = positionY
                + -Math.sin(isSidewalk ? viewAngle + Math.PI / 2 : viewAngle)
                * (isRunning ? SPEED * 2 : SPEED)
                * (isNegative ? -1 : 1);
        final double NEW_POSITION_X = positionX
                + Math.cos(isSidewalk ? viewAngle + Math.PI / 2 : viewAngle)
                * (isRunning ? SPEED * 2 : SPEED)
                * (isNegative ? -1 : 1);

        if (MapSystem.getCharAt(NEW_POSITION_Y, positionX) != ElementChar.WALL
                && MapSystem.getCharAt(NEW_POSITION_Y, positionX) != ElementChar.EXIT) {
            positionY = NEW_POSITION_Y;
        }

        if (MapSystem.getCharAt(positionY, NEW_POSITION_X) != ElementChar.WALL
                && MapSystem.getCharAt(positionY, NEW_POSITION_X) != ElementChar.EXIT) {
            positionX = NEW_POSITION_X;
        }
    }

    public boolean isRunning(boolean runPressed) {
        if (runPressed) {
            if (stamina > 0) {
                stamina = Math.max(stamina - STAMINA_DECR_RATIO, 0);
                return true;
            }
        } else if (stamina < MAX_STAMINA) {
            stamina = Math.min(stamina + STAMINA_INCR_RATIO, MAX_STAMINA);
        }
        return false;
    }

    public void changeViewAngle(double value) {
        viewAngle += value;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public double getStamina() {
        return stamina;
    }
}

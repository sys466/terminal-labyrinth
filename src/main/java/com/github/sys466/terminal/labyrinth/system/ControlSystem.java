package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.gui.GUI;
import com.github.sys466.terminal.labyrinth.type.ControlType;
import lombok.experimental.UtilityClass;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ControlSystem {

    private final Map<ControlType, Boolean> CONTROL_MAP;

    private double mousePositionX = MouseInfo.getPointerInfo().getLocation().getX();

    static {
        ControlType[] controlTypes = ControlType.values();
        CONTROL_MAP = new HashMap<>(controlTypes.length);
        Arrays.stream(controlTypes).forEach(controlType -> CONTROL_MAP.put(controlType, false));
    }

    public void updateControlStatus(ControlType controlType, boolean value) {
        CONTROL_MAP.replace(controlType, value);
    }

    public void calculateMovement() {
        calculateMouseMovement();
        calculateKeyboardMovement();
    }

    private void calculateMouseMovement() {

        double mousePositionY = MouseInfo.getPointerInfo().getLocation().getY();
        double newMousePositionX = MouseInfo.getPointerInfo().getLocation().getX();
        double changeValue = Math.abs(newMousePositionX - mousePositionX) / 1000;

        UserSystem.changeViewAngle(newMousePositionX > mousePositionX ? changeValue : -changeValue);

        mousePositionX = newMousePositionX;

        if (mousePositionY > GUI.SCREEN_HEIGHT * 0.75
                || mousePositionY < GUI.SCREEN_HEIGHT * 0.25
                || mousePositionX > GUI.SCREEN_WIDTH * 0.75
                || mousePositionX < GUI.SCREEN_WIDTH * 0.25) {

            mousePositionY = GUI.SCREEN_HEIGHT * 0.5;
            mousePositionX = GUI.SCREEN_WIDTH * 0.5;
            GUI.moveMouseToPosition((int) mousePositionY, (int) mousePositionX);
        }
    }

    private void calculateKeyboardMovement() {

        boolean isRunning = UserSystem.isRunning(CONTROL_MAP.get(ControlType.RUN));

        if (CONTROL_MAP.get(ControlType.UP)) {
            UserSystem.changePosition(false, isRunning, false);
        }
        if (CONTROL_MAP.get(ControlType.DOWN)) {
            UserSystem.changePosition(true, isRunning, false);
        }
        if (CONTROL_MAP.get(ControlType.LEFT)) {
            UserSystem.changePosition(true, isRunning, true);
        }
        if (CONTROL_MAP.get(ControlType.RIGHT)) {
            UserSystem.changePosition(false, isRunning, true);
        }
    }
}

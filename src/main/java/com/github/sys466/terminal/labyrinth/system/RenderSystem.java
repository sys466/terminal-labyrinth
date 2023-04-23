package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.gui.GUI;
import com.github.sys466.terminal.labyrinth.map.Map;
import com.github.sys466.terminal.labyrinth.type.Message;
import com.github.sys466.terminal.labyrinth.type.ElementChar;
import com.github.sys466.terminal.labyrinth.type.RenderChar;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class RenderSystem {

    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(8);
    private final ConcurrentHashMap<Integer, Boolean> SCREEN_TASKS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, StringBuilder> SCREEN_DATA = new ConcurrentHashMap<>();
    private final int SCREEN_HEIGHT = 40;
    private final int SCREEN_WIDTH = 120;

    public void runLogoRenderingCycle() {
        char[] messageChars = Message.LOGO.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            if (messageChars[i] == '#') {
                messageChars[i] = TerminalLabyrinthApp.random.nextInt(2) == 0
                        ? RenderChar.LOGO_1
                        : RenderChar.LOGO_2;
            }
        }
        GUI.updateViewPort(String.valueOf(messageChars));
        GUI.updateDescriptionInfo(Message.CREDENTIALS);
    }

    public void runMainRenderingCycle() {
        renderScreenData();
        GUI.updateViewPort(completeScreenData());
        GUI.updateStaminaInfo(prepareStaminaData());
        GUI.updateTimerInfo(prepareTimerData());
        GUI.updateControlInfo(Message.CONTROLS);
        GUI.updateDescriptionInfo(Message.DESCRIPTION);
        GUI.updateCardInfo(prepareCardData());
    }

    public void runDeathRenderingCycle(String message) {
        GUI.updateViewPort(renderMessageData(message, RenderChar.USER_HUNTER_1, RenderChar.USER_HUNTER_2));
    }

    public void runSurvivedRenderingCycle() {
        GUI.updateViewPort(renderMessageData(Message.SURVIVED, RenderChar.LOGO_1, RenderChar.LOGO_2));
    }

    private String renderMessageData(String message, char char1, char char2) {
        char[] messageChars = message.toCharArray();
        for (int i = 0; i < messageChars.length; i++) {
            if (messageChars[i] == '#') {
                messageChars[i] = TerminalLabyrinthApp.random.nextInt(2) == 0
                        ? char1
                        : char2;
            }
        }
        return String.valueOf(messageChars);
    }

    private void renderScreenData() {

        resetScreenData();

        for (int w = 0; w < SCREEN_WIDTH; w++) {
            int finalW = w;
            EXECUTOR_SERVICE.execute(() -> renderVerticalLine(finalW, SCREEN_DATA.get(finalW)));
        }

        while (true) {
            if (checkScreenTasks()) {
                break;
            }
        }
    }

    private void resetScreenData() {

        SCREEN_DATA.clear();
        SCREEN_TASKS.clear();

        for (int w = 0; w < SCREEN_WIDTH; w++) {

            StringBuilder verticalLine = new StringBuilder();

            for (int h = 0; h < SCREEN_HEIGHT / 2; h++) {
                if (h < SCREEN_HEIGHT * 0.125) {
                    verticalLine.append("=");
                } else  if (h < SCREEN_HEIGHT * 0.25) {
                    verticalLine.append("-");
                } else if (h < SCREEN_HEIGHT * 0.375) {
                    verticalLine.append(".");
                } else {
                    verticalLine.append(" ");
                }
            }

            SCREEN_DATA.put(w, verticalLine);
            SCREEN_TASKS.put(w, false);
        }
    }

    private void renderVerticalLine(int w, StringBuilder verticalLine) {

        final double POSITION_Y = UserSystem.getPositionY();
        final double POSITION_X = UserSystem.getPositionX();

        final double FOV_VECTOR = UserSystem.getViewAngle() - UserSystem.FOV / 2 + UserSystem.FOV / SCREEN_WIDTH * w;
        final double FOV_VECTOR_Y = -Math.sin(FOV_VECTOR);
        final double FOV_VECTOR_X = Math.cos(FOV_VECTOR);

        boolean isReachedWall = false;
        boolean isReachedExit = false;
        boolean isReachedCard = false;
        boolean isReachedHunter = false;

        double wallExitDistance = 0;
        double cardDistance = 0;
        double hunterDistance = 0;

        while (wallExitDistance < UserSystem.VIEW_DISTANCE) {

            // Calculating fov vector's position
            wallExitDistance += 0.1;
            double positionYChange = POSITION_Y + FOV_VECTOR_Y * wallExitDistance;
            double positionXChange = POSITION_X + FOV_VECTOR_X * wallExitDistance;

            // Checking if fov vector reached wall
            if (MapSystem.getCharAt(positionYChange, positionXChange) == ElementChar.WALL) {
                isReachedWall = true;
                break;
             // Checking if fov vector reached exit
            } else if (MapSystem.getCharAt(positionYChange, positionXChange) == ElementChar.EXIT) {
                isReachedExit = true;
                break;
            }

            // Checking if fov vector reached card
            if (!isReachedCard) {
                if (CardSystem.isVectorReachedCard(positionYChange, positionXChange)) {
                    isReachedCard = true;
                    cardDistance = wallExitDistance;
                }
            }

            // Checking if fov vector reached hunter
            if (!isReachedHunter) {
                if (HunterSystem.isVectorReachedHunter(positionYChange, positionXChange)) {
                    isReachedHunter = true;
                    hunterDistance = wallExitDistance;
                }
            }
        }

        // Drawing wall or exit
        if (isReachedWall || isReachedExit) {
            int wallOrExitHeight = (int) (SCREEN_HEIGHT / 2 - SCREEN_HEIGHT / 2 / (wallExitDistance * 1.25));
            if (wallOrExitHeight < 1) {
                wallOrExitHeight = 1;
            }

            for (int h = wallOrExitHeight; h < SCREEN_HEIGHT / 2; h++) {
                if (wallOrExitHeight < SCREEN_HEIGHT * 0.125) {
                    verticalLine.setCharAt(h, isReachedWall ? RenderChar.WALL_NEAREST : RenderChar.EXIT_NEAREST);
                } else if (wallOrExitHeight < SCREEN_HEIGHT * 0.25) {
                    verticalLine.setCharAt(h, isReachedWall ? RenderChar.WALL_NEAR : RenderChar.EXIT_NEAR);
                } else if (wallOrExitHeight < SCREEN_HEIGHT * 0.375) {
                    verticalLine.setCharAt(h, isReachedWall ? RenderChar.WALL_DISTANT : RenderChar.EXIT_DISTANT);
                } else {
                    verticalLine.setCharAt(h, RenderChar.WALL_EXIT_MOST_DISTANT);
                }
            }
        }

        // Drawing card
        if (isReachedCard) {
            int cardHeight;
            if (cardDistance > 6) {
                cardHeight = 1;
            } else if (cardDistance > 4) {
                cardHeight = 2;
            } else if (cardDistance > 2) {
                cardHeight = 3;
            } else {
                cardHeight = 4;
            }
            cardHeight = SCREEN_HEIGHT / 2 - cardHeight;

            for (int h = cardHeight; h < SCREEN_HEIGHT / 2; h++) {
                verticalLine.setCharAt(h, cardDistance > 4 ? RenderChar.CARD_DISTANT : RenderChar.CARD_NEAR);
            }
        }

        // Drawing hunter
        if (isReachedHunter) {
            int hunterHeight = (int) (SCREEN_HEIGHT / 2 - SCREEN_HEIGHT / 2 / (hunterDistance * 1.25));
            if (hunterHeight < 1) {
                hunterHeight = 1;
            }

            for (int h = hunterHeight; h < SCREEN_HEIGHT / 2; h++) {
                verticalLine.setCharAt(h, hunterDistance > 4 ? RenderChar.HUNTER_DISTANT : RenderChar.HUNTER_NEAR);
            }
        }

        // Adding second half
        StringBuilder reversedVerticalLine = new StringBuilder(verticalLine);
        reversedVerticalLine.reverse();
        verticalLine.append(reversedVerticalLine);

        // Closing task
        SCREEN_TASKS.put(w, true);
    }

    private boolean checkScreenTasks() {
        return SCREEN_TASKS.values().stream().allMatch(Boolean.TRUE::equals);
    }

    private String completeScreenData() {

        if (TerminalLabyrinthApp.RENDER_MAP) {
            for (int h = Map.MAP_HEIGHT - 1; h >= 0; h--) {
                for (int w = Map.MAP_WIDTH - 1; w >= 0; w--) {
                    if ((int) UserSystem.getPositionY() == h && (int) UserSystem.getPositionX() == w) {
                        SCREEN_DATA.get(h).setCharAt(w, ElementChar.USER);
                    } else if (HunterSystem.isHunterOnPosition(h, w)) {
                        SCREEN_DATA.get(h).setCharAt(w, ElementChar.HUNTER);
                    } else if (CardSystem.isCardOnPosition(h, w)) {
                        SCREEN_DATA.get(h).setCharAt(w, ElementChar.CARD);
                    } else {
                        SCREEN_DATA.get(h).setCharAt(w, MapSystem.getCharAt(h, w));
                    }
                }
            }
        }

        StringBuilder finalScreen = new StringBuilder();
        for (int h = 0; h < SCREEN_HEIGHT; h++) {
            for (int w = 0; w < SCREEN_WIDTH; w++) {
                finalScreen.append(SCREEN_DATA.get(w).charAt(h));
            }
            finalScreen.append("\n");
        }
        return finalScreen.toString();
    }

    private String prepareStaminaData() {
        double current = UserSystem.getStamina() * 100 / UserSystem.MAX_STAMINA;
        StringBuilder text = new StringBuilder("Stamina:\n[");
        for (int i = 0; i < 100; i += 4) {
            if (i < current) {
                text.append(RenderChar.STAMINA_FULL);
            } else {
                text.append(RenderChar.STAMINA_EMPTY);
            }
        }
        text.append("]");
        return text.toString();
    }

    private String prepareTimerData() {
        return String.format("Time left:\n%02d:%02d",
                TimerSystem.getMinutesLeft(),
                TimerSystem.getSecondsLeft());
    }

    private String prepareCardData() {
        return String.format("Cards:\n%d / %d",
                TerminalLabyrinthApp.CARD_NUMBER - CardSystem.getCardsLeft(),
                TerminalLabyrinthApp.CARD_NUMBER);
    }
}

package com.github.sys466.terminal.labyrinth.unit;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.map.Map;
import com.github.sys466.terminal.labyrinth.system.MapSystem;
import com.github.sys466.terminal.labyrinth.system.SoundSystem;
import com.github.sys466.terminal.labyrinth.system.UserSystem;
import com.github.sys466.terminal.labyrinth.type.ElementChar;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class Hunter {

    public static final double HUNTER_SIZE = 0.1;

    private static double speed = 0.02;

    private final Clip SPOTTED_CLIP;
    private final FloatControl SPOTTED_VOLUME_CONTROL;
    private final FloatControl SPOTTED_PAN_CONTROL;
    private final Clip NAVIGATING_CLIP;
    private final FloatControl NAVIGATING_VOLUME_CONTROL;
    private final FloatControl NAVIGATING_PAN_CONTROL;

    private double positionY;
    private double positionX;
    private int oldPositionY;
    private int oldPositionX;
    private int direction;
    private double stepY;
    private double stepX;
    private double distance;
    private boolean spotted;
    private boolean chasing;
    private double lastSeenPositionY;
    private double lastSeenPositionX;

    {
        initPosition();

        direction = TerminalLabyrinthApp.random.nextInt(4);

        calculateStep();

        chasing = false;

        try (InputStream is = Hunter.class.getClassLoader().getResourceAsStream("./sound/hunter_spotted.wav");
             InputStream bis = new BufferedInputStream(is);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

            SPOTTED_CLIP = AudioSystem.getClip();
            SPOTTED_CLIP.open(ais);
            SPOTTED_VOLUME_CONTROL = (FloatControl) SPOTTED_CLIP.getControl(FloatControl.Type.MASTER_GAIN);
            SPOTTED_PAN_CONTROL = (FloatControl) SPOTTED_CLIP.getControl(FloatControl.Type.PAN);

        } catch (Throwable t) {
            throw new RuntimeException("error during hunter spotted sound initialization");
        }

        try (InputStream is = Hunter.class.getClassLoader().getResourceAsStream("./sound/hunter_navigating.wav");
             InputStream bis = new BufferedInputStream(is);
             AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

            NAVIGATING_CLIP = AudioSystem.getClip();
            NAVIGATING_CLIP.open(ais);
            NAVIGATING_VOLUME_CONTROL = (FloatControl) NAVIGATING_CLIP.getControl(FloatControl.Type.MASTER_GAIN);
            NAVIGATING_PAN_CONTROL = (FloatControl) NAVIGATING_CLIP.getControl(FloatControl.Type.PAN);

        } catch (Throwable t) {
            throw new RuntimeException("error during hunter navigating sound initialization");
        }
    }

    public void move() {

        hunt();

        if (spotted) {
            chasing = true;
        }

        if (!spotted
                && chasing
                && Math.abs(lastSeenPositionY - positionY) <= UserSystem.USER_SIZE + stepY
                && Math.abs(lastSeenPositionX - positionX) <= UserSystem.USER_SIZE + stepY) {

            chasing = false;
            navigate();
        }

        if (!chasing
                && ((int) positionY != oldPositionY
                || (int) positionX != oldPositionX)) {

            navigate();
        }

        positionY += stepY;
        positionX += stepX;

        updateVolume();
    }

    public static void increaseSpeed() {
        speed += 0.01;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getPositionX() {
        return positionX;
    }

    public void mute() {
        SPOTTED_CLIP.stop();
        NAVIGATING_CLIP.stop();
    }

    private void hunt() {

        double userPositionY = UserSystem.getPositionY();
        double userPositionX = UserSystem.getPositionX();
        double differenceY = userPositionY - positionY;
        double differenceX = userPositionX - positionX;

        distance = Math.sqrt(Math.pow(differenceY, 2) + Math.pow(differenceX, 2));

        double vectorStepY = differenceY / distance;
        double vectorStepX = differenceX / distance;
        double step = 0;

        spotted = true;

        while ((int) (positionY + vectorStepY * step) != (int) userPositionY
                || (int) (positionX + vectorStepX * step) != (int) userPositionX) {

            if (MapSystem.getCharAt(positionY + vectorStepY * step,positionX + vectorStepX * step) != ElementChar.EMPTY) {
                spotted = false;
                return;
            }

            step += 0.01;
        }

        stepY = vectorStepY * speed;
        stepX = vectorStepX * speed;
        lastSeenPositionY = userPositionY;
        lastSeenPositionX = userPositionX;
    }

    private void navigate() {

        // 0 - UP, 1 - DOWN, 2 - LEFT, 3 - RIGHT
        ArrayList<Integer> paths = new ArrayList<>();

        if (direction != 1 && MapSystem.getCharAt(positionY - 1, positionX) == ElementChar.EMPTY) {
            paths.add(0);
        }
        if (direction != 0 && MapSystem.getCharAt(positionY + 1, positionX) == ElementChar.EMPTY) {
            paths.add(1);
        }
        if (direction != 3 && MapSystem.getCharAt(positionY, positionX - 1) == ElementChar.EMPTY) {
            paths.add(2);
        }
        if (direction != 2 && MapSystem.getCharAt(positionY, positionX + 1) == ElementChar.EMPTY) {
            paths.add(3);
        }

        if (paths.isEmpty()) {
            if (direction < 2) {
                direction = direction == 0 ? 1 : 0;
            } else {
                direction = direction == 2 ? 3 : 2;
            }
        } else {
            direction = paths.get(TerminalLabyrinthApp.random.nextInt(paths.size()));
        }

        calculateStep();

        oldPositionY = (int) positionY;
        oldPositionX = (int) positionX;
    }

    private void initPosition() {
        do {
            positionY = TerminalLabyrinthApp.random.nextInt(1,Map.MAP_HEIGHT - 1);
            positionX = TerminalLabyrinthApp.random.nextInt(1,Map.MAP_WIDTH - 1);
        } while (MapSystem.getCharAt(positionY, positionX) != ElementChar.EMPTY);
        oldPositionY = (int) positionY;
        oldPositionX = (int) positionX;
    }

    private void calculateStep() {
        switch (direction) {
            case 0 -> {
                stepY = -speed;
                stepX = 0;
            }
            case 1 -> {
                stepY = speed;
                stepX = 0;
            }
            case 2 -> {
                stepY = 0;
                stepX = -speed;
            }
            default -> {
                stepY = 0;
                stepX = speed;
            }
        }
    }

    private void updateVolume() {
        if (distance > SoundSystem.SOUND_DISTANCE) {
            SPOTTED_CLIP.stop();
            NAVIGATING_CLIP.stop();
        } else {
            float volume = SoundSystem.getVolumeByDistance(distance);
            float pan = SoundSystem.getPanByPosition(positionY, positionX);
            if (chasing) {
                SPOTTED_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
                SPOTTED_VOLUME_CONTROL.setValue(volume);
                SPOTTED_PAN_CONTROL.setValue(pan);
                NAVIGATING_CLIP.stop();
            } else {
                SPOTTED_CLIP.stop();
                NAVIGATING_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
                NAVIGATING_VOLUME_CONTROL.setValue(volume);
                NAVIGATING_PAN_CONTROL.setValue(pan);
            }
        }
    }
}

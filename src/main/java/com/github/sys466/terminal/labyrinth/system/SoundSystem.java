package com.github.sys466.terminal.labyrinth.system;

import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

@UtilityClass
public class SoundSystem {

    public final float MIN_VOLUME       = -80.0f;
    public final double SOUND_DISTANCE  = 18;

    private final Clip LOGO_CLIP;
    private final Clip BGM_CLIP;

    static {

        {
            try (InputStream is = SoundSystem.class.getClassLoader().getResourceAsStream("./sound/logo.wav");
                 InputStream bis = new BufferedInputStream(is);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

                LOGO_CLIP = AudioSystem.getClip();
                LOGO_CLIP.open(ais);

            } catch (Throwable t) {
                throw new RuntimeException("error during bgm initialization", t);
            }
        }

        {
            try (InputStream is = SoundSystem.class.getClassLoader().getResourceAsStream("./sound/bgm.wav");
                 InputStream bis = new BufferedInputStream(is);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {

                BGM_CLIP = AudioSystem.getClip();
                BGM_CLIP.open(ais);

            } catch (Throwable t) {
                throw new RuntimeException("error during bgm initialization", t);
            }
        }
    }

    public float getVolumeByDistance(double distance) {
        return (float) (distance * MIN_VOLUME / SOUND_DISTANCE);
    }

    public float getPanByPosition(double hunterPositionY, double hunterPositionX) {
        double positionY = UserSystem.getPositionY();
        double positionX = UserSystem.getPositionX();
        double viewAngle = UserSystem.getViewAngle();

        double degrees = Math.toDegrees(Math.atan2(hunterPositionY - positionY, hunterPositionX - positionX)
                - Math.atan2(-Math.sin(viewAngle), Math.cos(viewAngle)));

        if (degrees < 0) {
            degrees += 360;
        }

        float value;
        if ((int) degrees >= 270) {
            value = (float) (1 - degrees % 90 / 90);
        } else if ((int) degrees >= 180) {
            value = (float) (degrees % 90 / 90);
        } else if ((int) degrees >= 90) {
            value = (float) (1 - degrees % 90 / 90) * -1;
        } else {
            value = (float) (degrees % 90 / 90) * -1;
        }

        return value;
    }

    public void initLogoSound() {
        LOGO_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void initMainSound() {
        LOGO_CLIP.stop();
        BGM_CLIP.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void muteSound() {
        BGM_CLIP.stop();
        HunterSystem.muteHunters();
    }
}

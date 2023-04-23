package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.unit.Hunter;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class HunterSystem {

    private final Set<Hunter> HUNTERS = new HashSet<>(4);

    static {
        addHunter();
    }

    public void moveHunters() {
        HUNTERS.forEach(Hunter::move);
    }

    public boolean isVectorReachedHunter(double vectorPositionY, double vectorPositionX) {
        return HUNTERS.stream()
                .anyMatch(hunter -> Math.abs(vectorPositionY - hunter.getPositionY()) <= Hunter.HUNTER_SIZE
                        && Math.abs(vectorPositionX - hunter.getPositionX()) <= Hunter.HUNTER_SIZE);
    }

    public boolean isHunterOnPosition(int positionY, int positionX) {
        return HUNTERS.stream()
                .anyMatch(hunter -> (int) hunter.getPositionY() == positionY && (int) hunter.getPositionX() == positionX);
    }

    public boolean isHunterReachedUser() {
        return HUNTERS.stream()
                .anyMatch(hunter -> Math.abs(UserSystem.getPositionY() - hunter.getPositionY()) <= UserSystem.USER_SIZE
                        && Math.abs(UserSystem.getPositionX() - hunter.getPositionX()) <= UserSystem.USER_SIZE);
    }

    public void addHunter() {
        HUNTERS.add(new Hunter());
    }

    public void cardFound() {
        Hunter.increaseSpeed();
        addHunter();
    }

    public void muteHunters() {
        HUNTERS.forEach(Hunter::mute);
    }
}

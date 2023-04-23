package com.github.sys466.terminal.labyrinth.system;

import com.github.sys466.terminal.labyrinth.TerminalLabyrinthApp;
import com.github.sys466.terminal.labyrinth.map.Map;
import com.github.sys466.terminal.labyrinth.type.ElementChar;
import com.github.sys466.terminal.labyrinth.unit.Card;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class CardSystem {

    private final Set<Card> CARDS;

    static {

        CARDS = new HashSet<>(TerminalLabyrinthApp.CARD_NUMBER);

        int positionY;
        int positionX;

        for (int i = 0; i < TerminalLabyrinthApp.CARD_NUMBER; i++) {
            do {
                positionY = TerminalLabyrinthApp.random.nextInt(Map.MAP_HEIGHT - 1) + 1;
                positionX = TerminalLabyrinthApp.random.nextInt(Map.MAP_WIDTH - 1) + 1;
            } while (MapSystem.getCharAt(positionY, positionX) != ElementChar.EMPTY);

            CARDS.add(new Card(positionY + 0.5, positionX + 0.5));
        }
    }

    public boolean isVectorReachedCard(double vectorPositionY, double vectorPositionX) {
        return CARDS.stream()
                .anyMatch(card -> isReachedCard(card, vectorPositionY, vectorPositionX));
    }

    public void isUserReachedCard() {
        for (Card card : CARDS) {
            if (isReachedCard(card, UserSystem.getPositionY(), UserSystem.getPositionX())) {
                CARDS.remove(card);
                HunterSystem.cardFound();
                break;
            }
        }
    }

    public boolean isCardOnPosition(int positionY, int positionX) {
        return CARDS.stream()
                .anyMatch(card -> (int) card.getPositionY() == positionY && (int) card.getPositionX() == positionX);
    }

    public int getCardsLeft() {
        return CARDS.size();
    }

    private boolean isReachedCard(Card card, double positionY, double positionX) {
        return Math.abs(positionY - card.getPositionY()) <= Card.CARD_SIZE
                && Math.abs(positionX - card.getPositionX()) <= Card.CARD_SIZE;
    }
}

package org.example;

import java.util.Random;

public class GameSession {
    int playerScore;
    int dealerScore;
    boolean isPlaying;
    int[] deck;
    int currentCard;

    public GameSession() {
        initGame();
    }

    public void initGame() {
        isPlaying = false;
        deck = buildDeck();
        currentCard = 0;
        playerScore = 0;
        dealerScore = 0;
    }

    private static int[] buildDeck() {
        int[] deck = new int[36];
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 4; j++) {
                int currentElement = new Random().nextInt(deck.length);
                while (deck[currentElement] != 0) {
                    currentElement = new Random().nextInt(deck.length);
                }
                deck[currentElement] = i;
            }
        }
        return deck;
    }
}

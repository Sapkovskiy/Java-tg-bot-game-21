package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Random;

public class BotMain extends TelegramLongPollingBot {

    int playerScore;
    int dealerScore;
    boolean isPlaying;
    int[] deck;
    int currentCard;

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new BotMain());
        System.out.println("Bot started");
    }

    static int[] buildDeck() {
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

    void initGame() {
        isPlaying = false;
        deck = buildDeck();
        currentCard = 0;
        playerScore = 0;
        dealerScore = 0;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String textUsers = update.getMessage().getText();
        System.out.println(textUsers);

        if (textUsers.equals("/start")) {
            sendMsg(chatId, "Привет! Добро пожаловать в игру 21!");
            sendMsg(chatId, "Взять карту д/н? (y/n)");
            initGame();
            isPlaying = true;
        }
        if (!isPlaying) {
            sendMsg(chatId, "Чтобы начать игру введите /start");
            return;
        }
        if (textUsers.equals("y") || textUsers.equals("д")) {
            playerScore += deck[currentCard++];
            if (playerScore > 21) {
                while (dealerScore < 17){
                    dealerScore += deck[currentCard++];
                }
                isPlaying = false;
            }
            sendMsg(chatId, "Ваш счёт: " + playerScore);
            sendMsg(chatId, "Взять карту д/н? (y/n)");
        }
        if (textUsers.equals("n") || textUsers.equals("н")) {
            sendMsg(chatId, "Ход крупье . . .");
            while (dealerScore < 17) {
                dealerScore += deck[currentCard++];
            }
            isPlaying = false;
        }
        if (!isPlaying) {
            String scoreMessage = "Ваш счёт: " + playerScore + " Счёт крупье: " + dealerScore;
            if (dealerScore > 21 || playerScore <= 21 && playerScore > dealerScore) {
                sendMsg(chatId, "Вы победили!" + scoreMessage);
            } else if (dealerScore == playerScore) {
                sendMsg(chatId, "Ничья!" + scoreMessage);
            } else {
                sendMsg(chatId, "Победил крупье!" + scoreMessage);
            }
            sendMsg(chatId, "Игра окончена, чтобы сыграть ещё раз введите /start");
        }
    }

    void sendMsg(String chatId, String msg) {
        SendMessage sendMessage = new SendMessage(chatId, msg);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BotConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return BotConfig.getBotToken();
    }
}

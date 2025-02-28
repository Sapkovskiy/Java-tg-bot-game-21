package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BotMain extends TelegramLongPollingBot {
    private final ConcurrentHashMap<Long, GameSession> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new BotMain());
        System.out.println("Bot started");
    }

    private GameSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new GameSession());
    }

    @Override
    public void onUpdateReceived(Update update) {
        CompletableFuture.runAsync(() -> processUpdate(update));
    }

    private void processUpdate(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        String textUsers = update.getMessage().getText();

        GameSession session = getSession(chatId);

        if ("/start".equals(textUsers)) {
            session.initGame();
            sendMsg(chatId, "Привет! Добро пожаловать в игру 21!");
            sendMsg(chatId, "Взять карту д/н? (y/n)");
            session.isPlaying = true;
            return;
        }

        if (!session.isPlaying) {
            sendMsg(chatId, "Чтобы начать игру, введите /start");
            return;
        }

        if ("y".equalsIgnoreCase(textUsers) || "д".equalsIgnoreCase(textUsers)) {
            session.playerScore += session.deck[session.currentCard++];
            sendMsg(chatId, "Ваш счёт: " + session.playerScore);
            if (session.playerScore > 21) {
                endGame(chatId, session);
                return;
            }
            sendMsg(chatId, "Взять карту д/н? (y/n)");
        }

        if ("n".equalsIgnoreCase(textUsers) || "н".equalsIgnoreCase(textUsers)) {
            sendMsg(chatId, "Ход крупье . . .");
            while (session.dealerScore < 17) {
                session.dealerScore += session.deck[session.currentCard++];
            }
            endGame(chatId, session);
        }
    }

    private void endGame(Long chatId, GameSession session) {
        session.isPlaying = false;
        String scoreMessage = "Ваш счёт: " + session.playerScore + " Счёт крупье: " + session.dealerScore;
        if (session.dealerScore > 21 || session.playerScore <= 21 && session.playerScore > session.dealerScore) {
            sendMsg(chatId, "Вы победили! " + scoreMessage);
        } else if (session.dealerScore == session.playerScore) {
            sendMsg(chatId, "Ничья! " + scoreMessage);
        } else {
            sendMsg(chatId, "Победил крупье! " + scoreMessage);
        }
        sendMsg(chatId, "Игра окончена, чтобы сыграть ещё раз, введите /start");
    }

    private void sendMsg(Long chatId, String msg) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), msg);
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

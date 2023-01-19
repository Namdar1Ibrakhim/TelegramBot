package com.example.telegrambot;



import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private OurService ourService;

    final BotConfig config;

    static final String HELP_TEXT = "Приветствую тебя в боте SDU.\n\n" +
            "Вот список комманд:\n\n" +
            "Команда /start начать бота\n\n" +
            "Команда /weather определить погоду\n\n" +
            "Команда /week определит номер недели\n\n" +
            "Команда /help помощник";


    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/weather", "get weather"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/week", "week training"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;

                    case "/weather":
                            prepareAndSendMessage(chatId, "Погода в городе Алматы: " + ourService.pogoda());
                        System.out.println(update.getMessage().getChat().getFirstName());
                        break;
                    case "/week":
                        LocalDate myObj = LocalDate.now();
                        String[] otv = myObj.toString().split("-");
                        if(otv[1].equals("01") && Integer.parseInt(otv[2])<23){
                            prepareAndSendMessage(chatId, "Каникулы!");
                        }else if (otv[1].equals("01") && Integer.parseInt(otv[2])>=23){
                            prepareAndSendMessage(chatId, "Номер недели: #1");
                        }
                        break;
                    default:
                        prepareAndSendMessage(chatId, "Sorry, command was not recognized");
            }
        }


    }



    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + " :blush:";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }


    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
    }



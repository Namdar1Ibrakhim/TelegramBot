package com.example.telegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OurService {
    @Autowired
    private News news;

    @Autowired
    ParserTask parserTask;

    public String pogoda(){
//        return news.getTitle();
        return parserTask.parseNews();
    }
}

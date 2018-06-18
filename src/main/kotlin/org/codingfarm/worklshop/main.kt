package org.codingfarm.worklshop

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi

fun main(args: Array<String>) {
        ApiContextInitializer.init()                                                         // 1
        val api = TelegramBotsApi()                                                          // 2
        val bot = AxaLikeBot("599113394:AAHy8hGibbv0gfMIGosE-YhPV_fAXlVWS0U", "AxaDoSomethingbot")  // 3
        api.registerBot(bot)                                                                 // 4
    }
package org.codingfarm.worklshop

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.*
import org.telegram.abilitybots.api.util.AbilityUtils.getChatId
import org.telegram.telegrambots.api.objects.Update
import java.util.function.Consumer
import java.util.function.Predicate
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage

class AxaLikeBot(botUserName: String, botToken: String) : AbilityBot(botUserName,botToken) {

    private var client:MqttClient;

    init {
        client = MqttClient("tcp://iot.eclipse.org:1883", "0815")
        client.connect()
    }


    override fun creatorId(): Int {
        return 580924163
    }




    @Suppress("unused")
    fun like(): Ability {
        return Ability
                .builder()
                .name("like")
                .info("send a like")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action {context -> client.publish("paho/metest/axadodavote", MqttMessage("like".toByteArray())); silent.send("Danke für den Like", context.chatId())}
                .build()
    }

    @Suppress("unused")
    fun dislike(): Ability {
        return Ability
                .builder()
                .name("dislike")
                .info("send a dislike")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action {context -> client.publish("paho/metest/axadodavote", MqttMessage("dislike".toByteArray())); silent.send("schade ... ein dislike", context.chatId())}
                .build()
    }

    @Suppress("unused")
    fun sayHelloWorld(): Ability {
        return Ability
                .builder()
                .name("hello")                                                         // 1
                .info("says hello world")                                              // 2
                .locality(Locality.ALL)                                                         // 3
                .privacy(Privacy.PUBLIC)                                                       // 4
                .action { context -> silent.send("Hello world", context.chatId()) }  // 5
                .build()
    }

    @Suppress("unused")
    fun replyToPhoto() : Reply {
        return Reply.of(
                Consumer<Update> { update -> silent.send("Nice pic!", getChatId(update)) },
                Flag.PHOTO)
    }


    @Suppress("unused")
    fun sayHi(): Ability {
        return Ability
                .builder()
                .name("hi")
                .info("says hi")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action { context ->
                    val firstName = context.user().firstName()
                    silent.send("Hi, $firstName", context.chatId()!!)
                }
                .reply(
                        Consumer<Update> { update -> silent.send("Wow, nice name!", update.message.chatId!!) },
                        Flag.TEXT,
                        Predicate<Update> { update -> update.message.text.startsWith("/hi") },
                        isMarcus
                )
                .build()
    }

    @Suppress("unused")
    fun counter(): Ability {
        return Ability.builder()
                .name("count")
                .info("increments a counter per user")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .action { context ->
                    val counterMap = db.getMap<String, Int>("COUNTERS")
                    val userId = context.user().id()
                    val counter = counterMap.compute(userId.toString(), {_, c -> if (c == null) 1 else c + 1})
                    val message = String.format("%s, your count is now %d!",
                            context.user().shortName(), counter)
                    silent.send(message, context.chatId()!!)
                }
                .build()
    }

    private val isMarcus: Predicate<Update>
        get() = Predicate { update -> update.message.from.firstName.equals("Micha", ignoreCase = true) }
}
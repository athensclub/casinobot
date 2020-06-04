package athensclub.casinobot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.money.MoneyCommandManger;
import athensclub.casinobot.money.MoneyManager;
import athensclub.casinobot.room.RoomCommandManager;
import athensclub.casinobot.room.RoomManager;

public class Main {

    private static final MoneyManager money = new MoneyManager();
    private static final MoneyCommandManger manager = new MoneyCommandManger();
    private static final RoomCommandManager room = new RoomCommandManager();

    public static void main(String[] args) {

	DiscordApi api = new DiscordApiBuilder().setToken("NzExOTUwNjc4OTM4ODc3OTUz.XsKeLA.NQ1_Fzbb1T6NjV-DKm_krfkvpa0")
		.login().join();
	money.setApi(api);
	manager.setApi(api);
	manager.setMoney(money);
	room.setApi(api);
	room.setMoney(money);

	api.addMessageCreateListener(e -> {
	    String msg = e.getMessageContent();
	    if (msg.startsWith("$")) {
		String expr = msg.substring(1);
		if (manager.isValid(expr,e)) {
		    manager.run(expr, e);
		}else if(room.isValid(expr, e)) {
		    room.run(expr, e);
		}
	    }
	});

    }

}

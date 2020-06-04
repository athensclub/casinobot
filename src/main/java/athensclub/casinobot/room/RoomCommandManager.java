package athensclub.casinobot.room;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import athensclub.casinobot.SimpleCommandManager;
import athensclub.casinobot.money.MoneyManager;

/**
 * A class that manages room for each channel.
 * 
 * @author Athensclub
 *
 */
public class RoomCommandManager extends SimpleCommandManager {

    private static String helpTemplate;

    static {
	StringBuilder result = new StringBuilder();
	result.append(MoneyManager.helpInfo());
	result.append(RoomManager.helpInfo());
	helpTemplate = result.toString();
    }

    private RoomManager rooms = new RoomManager();

    private MoneyManager money;

    {
	addCommand("host", (e, args) -> {
	    rooms.host(e, args);
	});
	setRoleRequirements("host", "Dealer");

	addCommand("remove", (e, args) -> {
	    rooms.remove(e);
	});
	setRoleRequirements("remove", "Dealer");

	addCommand("room", (e, args) -> {
	    Room room = rooms.getRoom(e.getChannel());
	    switch (args.get(0).toLowerCase()) {
	    case "-members":
		info(room.getRoomName() + " Members", room.memberList()).send(e.getChannel());
		break;
	    default:
		throw new IllegalArgumentException("unknown room argument: " + args.get(0));
	    }
	});

	addCommand("join", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    Room room = rooms.getRoom(e.getChannel());
	    room.acceptUser(user.getId(), args);
	    info("Room enter", user.getNicknameMentionTag() + " joined " + room.getRoomName()).send(e.getChannel());
	});

	addCommand("leave", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    Room room = rooms.getRoom(e.getChannel());
	    room.removeUser(user.getId());
	    info("Room exit", user.getNicknameMentionTag() + " left " + room.getRoomName()).send(e.getChannel());
	});

	addCommand("help", (e, args) -> {
	    Room room = rooms.getRoom(e.getChannel());
	    StringBuilder result = new StringBuilder(helpTemplate);
	    if (room != null)
		result.append(room.getHelpInfo());
	    info("Help",result.toString()).send(e.getChannel());
	});
    }

    /**
     * Set money manager used by this manager.
     * 
     * @param money
     */
    public void setMoney(MoneyManager money) {
	this.money = money;
	rooms.setMoney(money);
    }

    /**
     * Get money manager used by this manager.
     * 
     * @return
     */
    public MoneyManager getMoney() {
	return money;
    }

    @Override
    public void setApi(DiscordApi api) {
	super.setApi(api);
	rooms.setApi(api);
    }

    @Override
    public boolean isValid(String expr, MessageCreateEvent e) {
	if (super.isValid(expr, e))
	    return true;
	return rooms.isValidRoomCommand(expr, e);
    }

    @Override
    public void run(String expr, MessageCreateEvent e) {
	if (super.isValid(expr, e))
	    super.run(expr, e);
	else
	    rooms.runRoomCommand(expr, e);
    }

}

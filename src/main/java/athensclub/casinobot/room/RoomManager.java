package athensclub.casinobot.room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import athensclub.casinobot.UtilCommands;
import athensclub.casinobot.money.MoneyManager;
import athensclub.casinobot.room.poker.PokerRoom;

/**
 * A class for managing all the rooms.
 * 
 * @author Athensclub
 *
 */
public class RoomManager implements UtilCommands {

    private DiscordApi api;

    private MoneyManager money;

    private HashMap<TextChannel, Room> rooms = new HashMap<>();

    private HashMap<String, Supplier<Room>> factory = new HashMap<>();

    private static String helpInfo;

    static {
	try (BufferedReader br = new BufferedReader(
		new InputStreamReader(RoomCommandManager.class.getResourceAsStream("help-info.txt")))) {
	    String temp = null;
	    StringBuilder result = new StringBuilder();
	    while ((temp = br.readLine()) != null)
		result.append(temp).append('\n');
	    helpInfo = result.toString();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * A string to be shown when $help is called.
     * 
     * @return
     */
    public static String helpInfo() {
	return helpInfo;
    }

    {
	factory.put("poker", () -> new PokerRoom());
    }

    /**
     * Host the room with the first element of args as room type.
     * 
     * @param e
     * @param args
     */
    public void host(MessageCreateEvent e, List<String> args) {
	User user = e.getMessageAuthor().asUser().get();
	if (rooms.get(e.getChannel()) != null)
	    throw new IllegalArgumentException(
		    user.getNicknameMentionTag() + " can not host a room in this channel, the room already exist");
	if (!factory.containsKey(args.get(0).toLowerCase()))
	    throw new IllegalArgumentException("Unknown room type: " + args.get(0));
	Room room = factory.get(args.get(0).toLowerCase()).get();
	room.setApi(api);
	room.setMoney(money);
	room.onCreate(e, args.subList(1, args.size()));
	rooms.put(e.getChannel(), room);
	info("Room Created", user.getNicknameMentionTag() + " hosted " + room.getRoomName()).send(e.getChannel());
    }

    /**
     * Remove a room from a channel called by the user
     * 
     * @param e
     */
    public void remove(MessageCreateEvent e) {
	Room room = rooms.get(e.getChannel());
	if (room != null) {
	    rooms.put(e.getChannel(), null);
	    room.onRemove();
	    info("Room Removed",
		    e.getMessageAuthor().asUser().get().getNicknameMentionTag() + " removed " + room.getRoomName())
			    .send(e.getChannel());
	}
    }

    /**
     * Get the room of the given text channel.
     * 
     * @param c
     * @return
     */
    public Room getRoom(TextChannel c) {
	return rooms.get(c);
    }

    /**
     * Check whether the given command is valid for room in the caller's text
     * channel.
     * 
     * @param expr
     * @param e
     * @return
     */
    public boolean isValidRoomCommand(String expr, MessageCreateEvent e) {
	Room room = rooms.get(e.getChannel());
	if (room != null) {
	    if (room.getCommand().isValid(expr, e))
		return true;
	}
	return false;
    }

    /**
     * Run the room command of the room in the channel that the caller's is calling
     * from.
     * 
     * @param expr
     * @param e
     */
    public void runRoomCommand(String expr, MessageCreateEvent e) {
	rooms.get(e.getChannel()).getCommand().run(expr, e);
    }

    /**
     * Get the money manager used by this manager.
     * 
     * @return
     */
    public MoneyManager getMoney() {
	return money;
    }

    /**
     * Set the money manager used by this manager.
     * 
     * @param money
     */
    public void setMoney(MoneyManager money) {
	this.money = money;
    }

    /**
     * Set the discord api used by this manager.
     * 
     * @param api
     */
    public void setApi(DiscordApi api) {
	this.api = api;
    }

    /**
     * Get the discord api used by this manager.
     * 
     * @return
     */
    public DiscordApi getApi() {
	return api;
    }

}

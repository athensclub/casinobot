package athensclub.casinobot.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

import athensclub.casinobot.CommandManager;
import athensclub.casinobot.money.MoneyManager;

/**
 * A base class for all rooms.
 * 
 * @author Athensclub
 *
 */
public abstract class Room {

    private CommandManager command;

    private DiscordApi api;

    private MoneyManager money;

    private ArrayList<Long> roomMembers = new ArrayList<>();

    /**
     * Accept the given user to this room. throwing exception if not possible.
     * Accepting argument from joining user.
     * 
     * @param user
     */
    public void acceptUser(long id, List<String> args) {
	if (!roomMembers.contains(id))
	    roomMembers.add(id);
    }

    /**
     * Return a string that lists all member in this room
     * 
     * @return
     */
    public String memberList() {
	return roomMembers.stream().map(id -> {
	    try {
		return api.getUserById(id).get().getNicknameMentionTag();
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    return "";
	}).collect(Collectors.joining("\n"));
    }

    /**
     * Get the mutable list containing all the member in this room.
     * 
     * @return
     */
    protected ArrayList<Long> getRoomMembers() {
	return roomMembers;
    }

    /**
     * Get the stream of all the user in this room
     * 
     * @return
     */
    public Stream<Long> members() {
	return roomMembers.stream();
    }

    /**
     * Remove the given user from the room
     * 
     * @param user
     */
    public void removeUser(long id) {
	roomMembers.remove(id);
    }

    /**
     * Called when this room is getting removed.
     */
    public abstract void onRemove();

    /**
     * Called once when this room is created.
     * 
     * @param args
     */
    public abstract void onCreate(MessageCreateEvent e, List<String> args);

    /**
     * Get the name for this room type (ex "Poker Room")
     * 
     * @return
     */
    public abstract String getRoomName();

    /**
     * Return the text message when $help is called.
     * 
     * @return
     */
    public abstract String getHelpInfo();

    /**
     * Set the discord api used by this room.
     * 
     * @param api
     */
    public void setApi(DiscordApi api) {
	this.api = api;
    }

    /**
     * Get the discord api used by this room.
     * 
     * @return
     */
    public DiscordApi getApi() {
	return api;
    }

    /**
     * Set the global money manager for this room
     * 
     * @param money
     */
    public void setMoney(MoneyManager money) {
	this.money = money;
    }

    /**
     * Get the global money manager for this room.
     * 
     * @return
     */
    public MoneyManager getMoney() {
	return money;
    }

    /**
     * Get the command manager for this room.
     * 
     * @return
     */
    public CommandManager getCommand() {
	return command;
    }

    /**
     * Set the command manager for this room.
     * 
     * @param command
     */
    protected void setCommand(CommandManager command) {
	this.command = command;
    }

}

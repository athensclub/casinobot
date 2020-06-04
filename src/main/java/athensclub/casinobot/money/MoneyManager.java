package athensclub.casinobot.money;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;

import athensclub.casinobot.room.RoomCommandManager;

/**
 * A class for handling money system
 * 
 * @author Athensclub
 *
 */
public class MoneyManager {

    private HashMap<Long, Double> moneys = new HashMap<>();

    private double startingMoney = 1000;

    private DiscordApi api;
    
    private static String helpInfo;

    static {
	BufferedReader br = new BufferedReader(
		new InputStreamReader(MoneyManager.class.getResourceAsStream("help-info.txt")));
	String temp = null;
	StringBuilder result = new StringBuilder();
	try {
	    while ((temp = br.readLine()) != null)
		result.append(temp).append('\n');
	    br.close();
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

    /**
     * Set the discord api used by this manager
     * 
     * @param api
     */
    public void setApi(DiscordApi api) {
	this.api = api;
    }

    /**
     * Get the current starting money
     * 
     * @return
     */
    public double getStartingMoney() {
	return startingMoney;
    }

    /**
     * Set the starting money for new user to be the given amount
     * 
     * @return
     */
    public void setStartingMoney(double startingMoney) {
	this.startingMoney = startingMoney;
    }

    /**
     * Transfer money from one user to another user.
     * 
     * @param idFrom
     * @param toId
     * @param amt
     */
    public void transfer(long idFrom, long idTo, double amt) {
	if (amt < 0) {
	    try {
		throw new IllegalArgumentException(api.getUserById(idFrom).get().getNicknameMentionTag()
			+ " transfer money with negative value: $" + amt);
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    return;
	}
	if (getMoneyOf(idFrom) < amt) {
	    try {
		throw new IllegalArgumentException(api.getUserById(idFrom).get().getNicknameMentionTag()
			+ " does not have enough money to transfer: $" + amt);
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    return;
	}
	addMoney(idFrom, -amt);
	addMoney(idTo, amt);
    }

    /**
     * Add the given amount of money to the given user id.
     * 
     * @param id
     * @param amt
     */
    public void addMoney(long id, double amt) {
	moneys.put(id, getMoneyOf(id) + amt);
    }

    /**
     * Take the money from the given user, throwing exception if the user does not
     * have enough money
     * 
     * @param id
     * @param amt
     */
    public void takeMoney(long id, double amt) {
	if (getMoneyOf(id) < amt)
	    try {
		throw new IllegalArgumentException(
			api.getUserById(id).get().getNicknameMentionTag() + " can not afford $" + amt);
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
		return;
	    }
	addMoney(id, -amt);
    }

    /**
     * Take all the money from the given user, and return it.
     * 
     * @return
     */
    public double takeAllMoneyOf(long id) {
	double val = getMoneyOf(id);
	moneys.put(id, 0.0);
	return val;
    }

    /**
     * Get amount of money of the given user.
     * 
     * @param id
     * @return
     */
    public double getMoneyOf(long id) {
	Double val = moneys.get(id);
	if (val == null) {
	    val = startingMoney;
	    moneys.put(id, val);
	}
	return val;
    }

}

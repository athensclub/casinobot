package athensclub.casinobot.room.poker;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import athensclub.casinobot.UtilCommands;
import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Deck;
import athensclub.casinobot.card.image.CardImages;
import athensclub.casinobot.money.MoneyManager;
import athensclub.casinobot.room.Room;
import athensclub.casinobot.room.poker.hand.PokerHand;

/**
 * Represent a poker room.
 * 
 * @author Athensclub
 *
 */
public class PokerRoom extends Room implements UtilCommands {

    private static String helpInfo;

    static {
	BufferedReader br = new BufferedReader(
		new InputStreamReader(PokerRoom.class.getResourceAsStream("help-info.txt")));
	StringBuilder result = new StringBuilder();
	String temp = null;
	try {
	    while ((temp = br.readLine()) != null)
		result.append(temp).append('\n');
	    helpInfo = result.toString();
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private int maxPlayer = 8;

    private PokerCommandManager manager;

    private double minBuyin, maxBuyin, smallBlind, bigBlind;

    private MoneyManager stack;

    private HashMap<Long, List<Card>> hands = new HashMap<>();

    private PokerRound round = PokerRound.PREFLOP;

    private ArrayList<Card> community = new ArrayList<>();

    private ArrayList<Long> activePlayer = new ArrayList<Long>() {
	@Override
	public boolean remove(Object o) {
	    System.out.println("REMOVING " + o);
	    return super.remove(o);
	};
    };

    private HashMap<Long, Double> bet = new HashMap<>();

    private boolean over = true;

    private boolean roundOver = false;

    private boolean firstActed = false;

    private double potSize = 0;

    private double maxBetted = 0, maxBettedThisGame = 0;

    private int current;

    private int target;

    private int firstToAct;

    private int prevFirstToAct = 0;

    private TextChannel channel;

    private Deck deck = new Deck();

    // private

    {
	manager = new PokerCommandManager();
	manager.setRoom(this);
	stack = new MoneyManager();
	stack.setStartingMoney(0);
	setCommand(manager);
    }

    /**
     * Previous index of player relative to the idx
     * 
     * @param idx
     * @return
     */
    private int previous(int idx) {
	if (--idx < 0)
	    idx = activePlayer.size() + idx;
	return idx;
    }

    /**
     * Get the next actable player from the index
     * 
     * @param idx
     * @return
     */
    private int nextPlayer(int idx) {
	while (stack.getMoneyOf(activePlayer.get(idx = next(idx))) <= 0)
	    ;
	return idx;
    }

    /**
     * Next index of player relative to the idx
     * 
     * @param idx
     * @return
     */
    private int next(int idx) {
	if (++idx >= activePlayer.size())
	    idx %= activePlayer.size();
	return idx;
    }

    /**
     * Get the big blind amount for this room
     * 
     * @return
     */
    public double getBigBlind() {
	return bigBlind;
    }

    /**
     * Get the small blind amount for this poker room
     * 
     * @return
     */
    public double getSmallBlind() {
	return smallBlind;
    }

    /**
     * Get the maximum buy-in amount for this poker room
     * 
     * @return
     */
    public double getMaxBuyin() {
	return maxBuyin;
    }

    /**
     * Get the minimum buy-in amount for this poker room.
     * 
     * @return
     */
    public double getMinBuyin() {
	return minBuyin;
    }

    /**
     * Set the big blind amount for this poker room
     * 
     * @param bigBlind
     */
    public void setBigBlind(double bigBlind) {
	this.bigBlind = bigBlind;
    }

    /**
     * Set the small blind amount for this poker room
     * 
     * @param smallBlind
     */
    public void setSmallBlind(double smallBlind) {
	this.smallBlind = smallBlind;
    }

    /**
     * Set the maximum buy-in amount for this poker room
     * 
     * @param maxBuyin
     */
    public void setMaxBuyin(double maxBuyin) {
	this.maxBuyin = maxBuyin;
    }

    /**
     * Set the minimum buy-in amount for this poker room
     * 
     * @param minBuyin
     */
    public void setMinBuyin(double minBuyin) {
	this.minBuyin = minBuyin;
    }

    /**
     * Get the maximum amount of bet the single player has made in this betting
     * round.
     * 
     * @return
     */
    public double getMaxBetted() {
	return maxBetted;
    }

    /**
     * Check whether it is the current turn for the given player.
     * 
     * @param id
     * @return
     */
    public boolean isTurn(long id) {
	return activePlayer.get(current) == id;
    }

    @Override
    public String memberList() {
	return getRoomMembers().stream().map(id -> {
	    try {
		return getApi().getUserById(id).get().getNicknameMentionTag() + " with $" + stack.getMoneyOf(id);
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    return null;
	}).collect(Collectors.joining("\n"));
    }

    /**
     * Get the amount of actable player left ie. the player that is playing and has
     * a stack of greater than $0
     * 
     * @return
     */
    private int actableCount() {
	return (int) activePlayer.stream().filter(l -> stack.getMoneyOf(l) > 0).count();
    }

    /**
     * Check whether we can skip to the river automatically.
     * 
     * @return
     */
    private boolean autoSkippable() {
	if (actableCount() > 1)
	    return false;
	if (actableCount() == 0)
	    return true;
	long id = activePlayer.stream().filter(l -> stack.getMoneyOf(l) > 0).collect(Collectors.toList()).get(0);
	return bet.get(id) >= maxBettedThisGame;
    }

    /**
     * A pair of player and hand
     * 
     * @author Athensclub
     *
     */
    private static class PlayerHand implements Comparable<PlayerHand> {
	long player;
	PokerHand hand;
	double paid = 0;

	public PlayerHand(long player, PokerHand hand) {
	    this.player = player;
	    this.hand = hand;
	}

	@Override
	public int compareTo(PlayerHand o) {
	    return hand.compareTo(o.hand);
	}
    }

    /**
     * Distribute pot evenly among the given list of players.
     * 
     * @param players
     */
    private void givePot(List<PlayerHand> players) {
	HashMap<Long, Double> copy = new HashMap<>(bet);
	bet.forEach((id, amt) -> {
	    for (PlayerHand player : players) {
		double pay = Math.min(copy.get(player.player), amt / players.size());
		player.paid += pay;
		bet.put(id, amt - pay);
		stack.addMoney(player.player, pay);
	    }
	});
    }

    /**
     * Find all the winners of the current pot.
     * 
     * @return
     */
    private List<PlayerHand> findWinners() {
	List<PlayerHand> players = activePlayer.stream().map(id -> {
	    List<Card> options = new ArrayList<>(hands.get(id));
	    options.addAll(community);
	    return new PlayerHand(id, PokerHand.getBestHand(options));
	}).sorted().collect(Collectors.toList());
	PlayerHand max = players.remove(players.size() - 1);
	List<PlayerHand> result = new ArrayList<>();
	result.add(max);
	while (players.size() > 0 && players.get(players.size() - 1).compareTo(max) == 0) {
	    result.add(players.remove(players.size() - 1));
	}
	return result;
    }

    /**
     * Check the actable player left and auto complete the game if neccessary.
     */
    private void checkActable() {
	if (activePlayer.size() == 1) {
	    PlayerHand ph = new PlayerHand(activePlayer.get(0), null);// not gonna use player hand strength //
								      // anyway
	    givePot(Arrays.asList(ph));
	    try {
		info("Round Over", getApi().getUserById(ph.player).get().getNicknameMentionTag() + " won $" + ph.paid
			+ " before showdown. Please wait for Dealer to start new game.").send(channel);
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    over = true;
	} else if (autoSkippable()) {
	    roundOver = true;
	    nextRound();
	}
    }

    /**
     * Show the player's hand.
     * 
     * @param id
     */
    public void show(long id) {
	try {
	    User user = getApi().getUserById(id).get();
	    new MessageBuilder()
		    .setEmbed(new EmbedBuilder().setColor(Color.GREEN).setTitle("Show Card")
			    .setDescription(user.getNicknameMentionTag() + "'s hand"))
		    .addAttachment(CardImages.merge(hands.get(id)), "card.png").send(channel);
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Check if the current betting round is over and response accordingly.
     */
    private void checkRoundOver() {
	if (current == target) {
	    if (!firstActed) {
		firstActed = true;
	    } else {
		roundOver = true;
	    }
	}
    }

    /**
     * Make the current user bet the given amount, or all-in if not enough.
     * 
     * @param id
     * @param amt
     */
    public void bet(double amt, boolean blind) {
	if (roundOver)
	    throw new IllegalArgumentException("The current betting round is over. Please wait for dealer to continue");
	long id = activePlayer.get(current);
	if (stack.getMoneyOf(id) <= amt) {
	    allIn();
	    return;
	}
	if (amt < 2 * maxBetted) {
	    throw new IllegalArgumentException("A raise must be at least 2 * maximum amount of money a player betted");
	}
	if (!blind)
	    checkRoundOver();
	boolean raise = false;
	stack.takeMoney(id, amt);
	bet.put(id, bet.getOrDefault(id, 0.0) + amt);
	potSize += amt;
	if (maxBetted < amt && !blind) {
	    target = current;
	    raise = true;
	}
	maxBettedThisGame = Math.max(maxBettedThisGame, bet.get(id));
	maxBetted = Math.max(maxBetted, amt);
	current = nextPlayer(current);
	if (!blind)
	    checkRoundOver();
	String action = blind ? "pay the blind of" : raise ? "raise to" : "bet";
	try {
	    info("Bet", getApi().getUserById(id).get().getNicknameMentionTag() + "  " + action + " $" + amt
		    + ", pot size is $" + potSize
		    + (blind ? ""
			    : (" " + getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
				    + "'s turn to act"))).send(channel);
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Make the current player call the bet.
     */
    public void call() {
	if (roundOver)
	    throw new IllegalArgumentException("The current betting round is over. Please wait for dealer to continue");
	long id = activePlayer.get(current);
	double amt = maxBettedThisGame - bet.get(id);
	/*
	 * try { System.out.println("CALL " + getApi().getUserById(id).get() + " WITH "
	 * + stack.getMoneyOf(id) + " AMT TO CALL: " + amt); } catch
	 * (InterruptedException | ExecutionException e1) { e1.printStackTrace(); }
	 */
	if (stack.getMoneyOf(id) <= amt) {
	    allIn();
	    return;
	}
	stack.takeMoney(id, amt);
	bet.put(id, bet.getOrDefault(id, 0.0) + amt);
	potSize += amt;
	checkRoundOver();
	current = nextPlayer(current);
	checkRoundOver();
	checkActable();
	try {
	    info("Call", getApi().getUserById(id).get().getNicknameMentionTag() + " Called $" + amt
		    + " more, pot size is $" + potSize
		    + (roundOver ? ""
			    : (" " + getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
				    + "'s turn to act"))).send(channel).join();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	if (roundOver && !over) {
	    nextRound();
	}
    }

    /**
     * Make the current player goes all-in.
     */
    public void allIn() {
	if (roundOver)
	    throw new IllegalArgumentException("The current betting round is over. Please wait for dealer to continue");
	long id = activePlayer.get(current);

	double amt = stack.takeAllMoneyOf(id);
	bet.put(id, bet.getOrDefault(id, 0.0) + amt);
	potSize += amt;
	/*try {
	    System.out.println("ALLIN " + getApi().getUserById(id).get());
	    System.out.println("CURRENT: " + current + " ACTABLE: " + actableCount() + " MAX BETTED THIS GAME: " + maxBettedThisGame + " ROUND OVER: " + roundOver);
	    for (long p : activePlayer) {
		System.out.println(getApi().getUserById(p).get() + " STACK: " + stack.getMoneyOf(p) + " BETTED: "
			+ bet.getOrDefault(p, 0.0));
	    }
	} catch (InterruptedException | ExecutionException e1) {
	    e1.printStackTrace();
	}*/
	checkRoundOver();
	if (maxBetted < amt)
	    target = current;
	maxBettedThisGame = Math.max(maxBettedThisGame, bet.get(id));
	maxBetted = Math.max(maxBetted, amt);
	current = nextPlayer(current);
	//System.out.println(" ROUND OVER: " + roundOver + " CURRENT: " + current + " TARGET: " + target);
	checkActable();
	checkRoundOver();
	try {
	    info("Bet", getApi().getUserById(id).get().getNicknameMentionTag() + " goes all in for $" + amt
		    + ", pot size is $" + potSize + " "
		    + getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag() + "'s turn to act")
			    .send(channel).join();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	if (roundOver && !over) {
	    nextRound();
	}
    }

    /**
     * Make the current player fold.
     */
    public void fold() {
	if (roundOver)
	    throw new IllegalArgumentException("The current betting round is over. Please wait for dealer to continue");
	long id = activePlayer.get(current);
	activePlayer.remove(current);
	checkRoundOver();
	checkActable();
	current = nextPlayer(current);
	checkRoundOver();
	try {
	    info("Fold",
		    getApi().getUserById(id).get().getNicknameMentionTag() + " Folded. "
			    + (roundOver ? ""
				    : (getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
					    + "'s turn to act.")));
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	if (roundOver && !over) {
	    nextRound();
	}
    }

    /**
     * Make the current player check
     */
    public void check() {
	System.out.println(
		"CHECK MBETTED: " + maxBettedThisGame + " ALREADY BETTED: " + bet.get(activePlayer.get(current)));
	if (roundOver)
	    throw new IllegalArgumentException("The current betting round is over. Please wait for dealer to continue");
	if (bet.get(activePlayer.get(current)) < maxBetted)
	    throw new IllegalArgumentException("You can not check this round, there is already a bet");
	checkRoundOver();
	current = nextPlayer(current);
	checkRoundOver();
	try {
	    info("Check", getApi().getUserById(activePlayer.get(previous(current))).get().getNicknameMentionTag()
		    + " checks"
		    + (roundOver ? ""
			    : (",\n" + getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
				    + "'s turn to act"))).send(channel).join();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
	if (roundOver && !over) {
	    nextRound();
	}
    }

    /**
     * Begin this poker round's preflop.
     */
    public void start() {
	if (!over)
	    throw new IllegalArgumentException("The current round is not over yet, Can not start a new round.");
	bet.clear();
	community.clear();
	activePlayer.clear();
	hands.clear();
	deck = new Deck();
	deck.shuffle();
	round = PokerRound.PREFLOP;
	activePlayer
		.addAll(getRoomMembers().stream().filter(id -> stack.getMoneyOf(id) > 0).collect(Collectors.toList()));
	if (activePlayer.size() < 2)
	    throw new IllegalArgumentException("Not enough players to start a poker game.");
	for (long player : activePlayer) {
	    hands.put(player, deck.deal(2));
	    try {
		new MessageBuilder()
			.setEmbed(new EmbedBuilder().setColor(Color.GREEN)
				.setTitle("Your hand on channel: " + channel.asServerTextChannel().get().getName()
					+ ", on server: " + channel.asServerChannel().get().getServer().getName()))
			.addAttachment(CardImages.merge(hands.get(player)), "card.png")
			.send(getApi().getUserById(player).get());
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	}
	roundOver = over = false;
	prevFirstToAct = firstToAct = nextPlayer(prevFirstToAct);
	potSize = maxBetted = maxBettedThisGame = 0;
	current = firstToAct;
	target = nextPlayer(nextPlayer(firstToAct));
	bet(smallBlind, true);
	bet(bigBlind, true);
	firstActed = false;
	try {
	    info("New Round",
		    "New round started, "
			    + getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
			    + "'s turn to act. Current pot: $" + potSize).send(channel);
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Progress the state to next round. Does nothing if the turn is over
     */
    public void nextRound() {
	if (over)
	    return;
	if (!roundOver)
	    throw new IllegalArgumentException(
		    "The current betting round is not over yet, can not move on to the next round");
	roundOver = false;
	if (round == PokerRound.RIVER) {
	    over = true;
	    List<PlayerHand> toDeclare = new ArrayList<>();
	    for (long id : activePlayer) {
		show(id);
	    }
	    while (activePlayer.size() > 0) {
		List<PlayerHand> winners = findWinners();
		givePot(winners);
		toDeclare.addAll(winners);
		activePlayer.removeAll(winners.stream().map(win -> win.player).collect(Collectors.toList()));
	    }
	    StringBuilder text = new StringBuilder();
	    for (PlayerHand h : toDeclare) {
		try {
		    if (h.paid > 0)
			text.append(getApi().getUserById(h.player).get().getNicknameMentionTag()).append(" won $")
				.append(h.paid).append(" with ").append(h.hand.toString()).append('\n');
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}
	    }
	    text.append("Please wait for the Dealer to start a new game");
	    info("Round Over", text.toString()).send(channel);
	} else {
	    community.addAll(deck.deal(round.getCardsDeal()));
	    maxBetted = 0;
	    round = round.getNext();
	    target = current = firstToAct;
	    firstActed = false;
	    try {
		new MessageBuilder().addAttachment(CardImages.merge(community), "card.png")
			.setEmbed(new EmbedBuilder().setColor(Color.GREEN).setTitle(round.toString()).setDescription(
				getApi().getUserById(activePlayer.get(current)).get().getNicknameMentionTag()
					+ "'s turn"))
			.send(channel).join();
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	    if (autoSkippable()) {
		roundOver = true;
		nextRound();
	    }
	}
    }

    @Override
    public void setApi(DiscordApi api) {
	super.setApi(api);
	manager.setApi(api);
	stack.setApi(api);
    }

    @Override
    public void acceptUser(long id, List<String> args) {
	User user = null;
	try {
	    user = getApi().getUserById(id).get();
	} catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	    return;
	}
	if (getRoomMembers().contains(id))
	    throw new IllegalArgumentException(user.getNicknameMentionTag() + " is already in " + getRoomName());
	if (getRoomMembers().size() >= maxPlayer)
	    throw new IllegalArgumentException(getRoomName() + " is already full");
	if (args.size() < 1)
	    throw new IllegalArgumentException(
		    user.getNicknameMentionTag() + " must enter buy-in amount to enter " + getRoomName());
	double amt = Double.parseDouble(args.get(0));
	if (amt > maxBuyin)
	    throw new IllegalArgumentException(user.getNicknameMentionTag() + " buy-in amount exceed " + getRoomName()
		    + " maximum buy-in amount: " + amt + " max: " + maxBuyin);
	if (amt < minBuyin)
	    throw new IllegalArgumentException(user.getNicknameMentionTag() + " buy-in amount does not reach "
		    + getRoomName() + " minimum buy in amount: " + amt + " min: " + minBuyin);
	getMoney().takeMoney(user.getId(), amt);
	stack.addMoney(user.getId(), amt);
	super.acceptUser(id, args);
    }

    @Override
    public void removeUser(long id) {
	getMoney().addMoney(id, stack.takeAllMoneyOf(id));
	if (activePlayer.remove(id) && !over) {
	    current %= activePlayer.size();
	    prevFirstToAct %= activePlayer.size();
	    firstToAct = activePlayer.size();
	    checkRoundOver();
	    checkActable();
	}
	super.removeUser(id);
    }

    @Override
    public void onRemove() {
	while (getRoomMembers().size() > 0) {
	    removeUser(getRoomMembers().get(getRoomMembers().size() - 1));
	}
    }

    @Override
    public void onCreate(MessageCreateEvent e, List<String> args) {
	Map<String, String> a = parseArgs(args, 0);
	String bb = a.get("bb");
	String sb = a.get("sb");
	if (bb == null && sb == null) {
	    bigBlind = 5;
	    smallBlind = 2;
	} else if (bb == null) {
	    smallBlind = Double.parseDouble(sb);
	    bigBlind = smallBlind * 2;
	} else if (sb == null) {
	    bigBlind = Double.parseDouble(bb);
	    smallBlind = bigBlind / 2;
	} else {
	    smallBlind = Double.parseDouble(sb);
	    bigBlind = Double.parseDouble(bb);
	    if (smallBlind * 2 > bigBlind)
		throw new IllegalArgumentException("Big blind must be at least 2 * small blind");
	}
	String minIn = a.get("minbuy");
	String maxIn = a.get("maxbuy");
	if (minIn == null && maxIn == null) {
	    minBuyin = 30 * bigBlind;
	    maxBuyin = 150 * bigBlind;
	} else if (minIn == null) {
	    maxBuyin = Double.parseDouble(maxIn);
	    if (maxBuyin < 10 * bigBlind) {
		throw new IllegalArgumentException("Poker room maximum buy-in must be greater than 10 * big blind");
	    }
	    minBuyin = maxBuyin / 5;
	} else if (maxIn == null) {
	    minBuyin = Double.parseDouble(minIn);
	    if (minBuyin < bigBlind) {
		throw new IllegalArgumentException("Poker room minimum buy-in must be greater than big blind");
	    }
	    maxBuyin = minBuyin * 5;
	} else {
	    minBuyin = Double.parseDouble(minIn);
	    maxBuyin = Double.parseDouble(maxIn);
	    if (maxBuyin < 10 * bigBlind) {
		throw new IllegalArgumentException("Poker room maximum buy-in must be greater than 10 * big blind");
	    }
	    if (minBuyin < bigBlind) {
		throw new IllegalArgumentException("Poker room minimum buy-in must be greater than big blind");
	    }
	}
	channel = e.getChannel();
    }

    @Override
    public String getRoomName() {
	return "Poker Room";
    }

    @Override
    public String getHelpInfo() {
	return helpInfo;
    }

}

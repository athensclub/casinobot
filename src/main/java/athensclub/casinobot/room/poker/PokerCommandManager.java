package athensclub.casinobot.room.poker;

import org.javacord.api.entity.user.User;

import athensclub.casinobot.SimpleCommandManager;

/**
 * A command manager for poker room
 * 
 * @author Athensclub
 *
 */
public class PokerCommandManager extends SimpleCommandManager {

    private PokerRoom room;

    /**
     * Set a poker room this manager is managing.
     * 
     * @param room
     */
    public void setRoom(PokerRoom room) {
	this.room = room;
    }

    {
	addCommand("start", (e, args) -> {
	    room.start();
	});
	setRoleRequirements("start", "Dealer");

	addCommand("next", (e, args) -> {
	    room.nextRound();
	});
	setRoleRequirements("next", "Dealer");

	addCommand("check", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    checkTurn(user);
	    room.check();
	});

	addCommand("bet", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    checkTurn(user);
	    room.bet(Double.parseDouble(args.get(0)),false);
	});
	
	addCommand("call", (e,args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    checkTurn(user);
	    room.call();
	});

	addCommand("allin", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    checkTurn(user);
	    room.allIn();
	});

	addCommand("fold", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    checkTurn(user);
	    room.fold();
	});
    }

    /**
     * Check if it is the user's turn to act. If not, exception is thrown.
     * 
     * @param user
     */
    private void checkTurn(User user) {
	if (!room.isTurn(user.getId()))
	    throw new IllegalArgumentException("It is not " + user.getNicknameMentionTag() + "'s turn to act");
    }

}

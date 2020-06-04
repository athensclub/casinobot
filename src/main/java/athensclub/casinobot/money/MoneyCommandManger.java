package athensclub.casinobot.money;

import org.javacord.api.entity.user.User;

import athensclub.casinobot.SimpleCommandManager;

/**
 * A command manager for money-based commands.
 * 
 * @author Athensclub
 *
 */
public class MoneyCommandManger extends SimpleCommandManager {

    private MoneyManager money;

    /**
     * Set the money manager for this command manager.
     * 
     * @param money
     */
    public void setMoney(MoneyManager money) {
	this.money = money;
    }

    {
	addCommand("money", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    info("Money View", user.getNicknameMentionTag() + " has: $" + money.getMoneyOf(user.getId()))
		    .send(e.getChannel());
	});

	addCommand("give", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    long target = id(args.get(0));
	    double amt = Double.parseDouble(args.get(1));
	    money.addMoney(target, amt);
	    info("Money Transaction", user.getNicknameMentionTag() + " gave $" + amt + " to " + args.get(0))
		    .send(e.getChannel());
	});
	setRoleRequirements("give", "Banker" );

	addCommand("pay", (e, args) -> {
	    User user = e.getMessageAuthor().asUser().get();
	    double amt = Double.parseDouble(args.get(1));
	    money.transfer(user.getId(), id(args.get(0)), amt);
	    info("Money Transaction", user.getNicknameMentionTag() + " paid $" + amt + " to " + args.get(0))
		    .send(e.getChannel());
	});
    }

}

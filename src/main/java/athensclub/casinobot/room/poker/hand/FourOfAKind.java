package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Represents a four of a kind hand.
 * 
 * @author Athensclub
 *
 */
public class FourOfAKind extends PokerHand {

    private Face high;

    private Face kicker;

    public FourOfAKind(Face high,Face kicker) {
	this.high = high;
	this.kicker = kicker;
	level = 9;
    }

    @Override
    public String toString() {
	return "Four of a kind, " + high.normalCasing() + " high";
    }

    /**
     * Get the best four-of-a-kind hand from the given list of 7 cards, returning
     * null if no four-of-a-kind hand can be made with the given list.
     * 
     * @param cards
     */
    public static FourOfAKind getHand(List<Card> cards) {
	for (Face f : Face.aceToTwo()) {
	    if (cards.stream().filter(c -> c.getFace() == f).count() >= 4) {
		Face kicker = cards.stream().filter(c -> c.getFace() != f).map(c -> c.getFace())
			.max(Face::compareByPokerValue).get();
		return new FourOfAKind(f, kicker);
	    }
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof FourOfAKind) {
	    FourOfAKind other = (FourOfAKind) o;
	    int x = Face.compareByPokerValue(high, other.high);
	    if(x != 0) return x;
	    return Face.compareByPokerValue(kicker, other.kicker);
	}
	return super.compareTo(o);
    }

}

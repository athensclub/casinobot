package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;
import athensclub.casinobot.card.Suit;

/**
 * Representing a straight flush hand.
 * 
 * @author Athensclub
 *
 */
public class StraightFlush extends PokerHand {

    private Face high;

    public StraightFlush(Face high) {
	this.high = high;
	level = 10;
    }

    @Override
    public String toString() {
	return "Straight Flush, " + high.normalCasing() + " high.";
    }

    /**
     * Create best straight flush hand from given 7 cards, or null if straight flush
     * can not be made with the given hand
     * 
     * @return
     */
    public static StraightFlush getHand(List<Card> cards) {
	for (Face face : Face.aceToSix()) {
	    for (Suit suit : Suit.values()) {
		boolean yes = true;
		Face temp = face;
		for (int i = 0; i < 5; i++) {
		    if (!cards.contains(new Card(temp, suit))) {
			yes = false;
			break;
		    }
		    temp = temp.getPrevious();
		}
		if (yes)
		    return new StraightFlush(face);
	    }
	}
	for (Suit suit : Suit.values()) {
	    boolean yes = true;
	    for (Face temp : Face.aceToFive()) {
		if (!cards.contains(new Card(temp, suit))) {
		    yes = false;
		    break;
		}
	    }
	    if (yes)
		return new StraightFlush(Face.FIVE);
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof StraightFlush) {
	    StraightFlush other = (StraightFlush) o;
	    return Face.compareByPokerValue(high, other.high);
	}
	return super.compareTo(o);
    }

}

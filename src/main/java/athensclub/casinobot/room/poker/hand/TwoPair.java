package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Represent a two pair hand
 * 
 * @author Athensclub
 *
 */
public class TwoPair extends PokerHand {

    private Face highPair, lowPair, kicker;

    public TwoPair(Face hi, Face lo, Face kick) {
	highPair = hi;
	lowPair = lo;
	kicker = kick;
	level = 4;
    }
    
    @Override
    public String toString() {
        return "Pair of " + highPair.normalCasing() + ", and pair of " + lowPair.normalCasing();
    }

    /**
     * Make a best possible two pair hand from a list of 7 cards, returning null if
     * two pair can not be made with the given hand.
     * 
     * @param cards
     * @return
     */
    public static TwoPair getHand(List<Card> cards) {
	Face[] a22 = Face.aceToTwo();
	for (int i = 0; i < a22.length - 1; i++) {
	    Face hi = a22[i];
	    if (cards.stream().filter(c -> c.getFace() == hi).count() >= 2) {
		for (int j = i + 1; j < a22.length; j++) {
		    Face lo = a22[j];
		    if (cards.stream().filter(c -> c.getFace() == lo).count() >= 2) {
			Face kicker = cards.stream().map(c -> c.getFace()).filter(f -> f != hi && f != lo)
				.max((a, b) -> Face.compareByPokerValue(a, b)).get();
			return new TwoPair(hi, lo, kicker);
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof TwoPair) {
	    TwoPair other = (TwoPair) o;
	    int x = Face.compareByPokerValue(highPair, other.highPair);
	    if (x != 0)
		return x;
	    x = Face.compareByPokerValue(lowPair, other.lowPair);
	    if (x != 0)
		return x;
	    return Face.compareByPokerValue(kicker, other.kicker);
	}
	return super.compareTo(o);
    }

}

package athensclub.casinobot.room.poker.hand;

import java.util.List;
import java.util.stream.Collectors;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Representing a single pair hand
 * 
 * @author Athensclub
 *
 */
public class Pair extends PokerHand {

    private Face high;

    private List<Face> kickers;

    @Override
    public String toString() {
	return "A pair of " + high.normalCasing();
    }

    public Pair(Face hi, List<Face> kick) {
	high = hi;
	kickers = kick;
	level = 3;
    }

    /**
     * Get the best possible single pair hand from the list of 7 cards, returning
     * null if a single pair hand can not be made.
     * 
     * @param cards
     * @return
     */
    public static Pair getHand(List<Card> cards) {
	for (Face hi : Face.aceToTwo()) {
	    if (cards.stream().filter(c -> c.getFace() == hi).count() >= 2) {
		List<Face> kickers = cards.stream().map(c -> c.getFace()).filter(f -> f != hi)
			.sorted((a, b) -> Face.compareByPokerValue(a, b)).collect(Collectors.toList());
		while (kickers.size() > 3)
		    kickers.remove(0);
		return new Pair(hi, kickers);
	    }
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof Pair) {
	    Pair other = (Pair) o;
	    int x = Face.compareByPokerValue(high, other.high);
	    if (x != 0)
		return x;
	    for (int i = 2; i >= 0; i--) {
		x = Face.compareByPokerValue(kickers.get(i), other.kickers.get(i));
		if (x != 0)
		    return x;
	    }
	    return 0;
	}
	return super.compareTo(o);
    }

}

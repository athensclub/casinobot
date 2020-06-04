package athensclub.casinobot.room.poker.hand;

import java.util.List;
import java.util.stream.Collectors;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;
import athensclub.casinobot.card.Suit;

/**
 * Representing a flush hand.
 * 
 * @author Athensclub
 *
 */
public class Flush extends PokerHand {

    private List<Face> highs;

    public Flush(List<Face> highs) {
	this.highs = highs;
	level = 7;
    }
    
    @Override
    public String toString() {
        return highs.get(4) + " high flush";
    }

    /**
     * Get the best possible flush hand from the list of 7 cards, returning null if
     * flush hand can not be created with the list of cards.
     * 
     * @param cards
     * @return
     */
    public static Flush getHand(List<Card> cards) {
	Flush bestSoFar = null;
	for (Suit s : Suit.values()) {
	    List<Face> highs = cards.stream().filter(c -> c.getSuit() == s).map(c -> c.getFace())
		    .sorted((a, b) -> Face.compareByPokerValue(a, b)).collect(Collectors.toList());
	    if (highs.size() < 5)
		continue;
	    while (highs.size() > 5)
		highs.remove(0);
	    Flush f = new Flush(highs);
	    if (bestSoFar == null)
		bestSoFar = f;
	    else if (f.compareTo(bestSoFar) > 0)
		bestSoFar = f;
	}
	return bestSoFar;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof Flush) {
	    Flush other = (Flush) o;
	    for (int i = 4; i >= 0; i--) {
		int x = Face.compareByPokerValue(highs.get(i), other.highs.get(i));
		if (x != 0)
		    return x;
	    }
	    return 0;
	}
	return super.compareTo(o);
    }

}

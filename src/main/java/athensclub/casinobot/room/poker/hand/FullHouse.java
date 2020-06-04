package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Representing a full house hand.
 * 
 * @author Athensclub
 *
 */
public class FullHouse extends PokerHand {

    private Face set;

    private Face pair;

    public FullHouse(Face set, Face pair) {
	this.set = set;
	this.pair = pair;
	level = 8;
    }
    
    @Override
    public String toString() {
        return set.normalCasing() + " full of " + pair.normalCasing();
    }

    /**
     * Get the best full house hand from the list of 7 cards, returning null if full
     * house can not be made with the given hand.
     * 
     * @param cards
     * @return
     */
    public static FullHouse getHand(List<Card> cards) {
	for(Face set = Face.ACE;set != null;set = set.getPrevious()) {
	    final Face seq = set;
	    if(cards.stream().filter(c -> c.getFace().equals(seq)).count() >= 3) {
		for(Face pair = Face.ACE;pair != null; pair = pair.getPrevious()) {
		    final Face peq = pair;
		    if(set != pair && cards.stream().filter(c -> c.getFace().equals(peq)).count() >= 2) 
			return new FullHouse(set, pair);
		}
	    }
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof FullHouse) {
	    FullHouse other = (FullHouse) o;
	    int x = Face.compareByPokerValue(set, other.set);
	    if (x != 0)
		return x;
	    return Face.compareByPokerValue(pair, other.pair);
	}
	return super.compareTo(o);
    }

}

package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * A class representing a straight hand.
 * 
 * @author Athensclub
 *
 */
public class Straight extends PokerHand {

    private Face high;

    public Straight(Face high) {
	this.high = high;
	level = 6;
    }
    
    @Override
    public String toString() {
        return high.normalCasing() + " high straight";
    }

    /**
     * Get the best straight hand from the list of 7 cards, returning null if
     * straight hand can not be made with the list of cards.
     * 
     * @param cards
     * @return
     */
    public static Straight getHand(List<Card> cards) {
	for (Face high : Face.aceToSix()) {
	    boolean yes = true;
	    Face temp = high;
	    for (int i = 0; i < 5; i++) {
		final Face teq = temp;
		if (!cards.stream().anyMatch(c -> c.getFace() == teq)) {
		    yes = false;
		    break;
		}
		temp = temp.getPrevious();
	    }
	    if (yes)
		return new Straight(high);
	}
	for (Face temp : Face.aceToFive()) {
	    if (!cards.stream().anyMatch(c -> c.getFace() == temp)) {
		return null;
	    }
	}
	return new Straight(Face.FIVE);
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof Straight) {
	    return Face.compareByPokerValue(high, ((Straight) o).high);
	}
	return super.compareTo(o);
    }

}

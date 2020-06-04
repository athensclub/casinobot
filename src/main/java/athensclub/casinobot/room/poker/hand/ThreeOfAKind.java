package athensclub.casinobot.room.poker.hand;

import java.util.List;
import java.util.stream.Collectors;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Represent three of a kind hand.
 * 
 * @author Athensclub
 *
 */
public class ThreeOfAKind extends PokerHand {

    private Face high;

    private List<Face> kickers;

    public ThreeOfAKind(Face high, List<Face> kickers) {
	this.high = high;
	this.kickers = kickers;
	level = 5;
    }
    
    @Override
    public String toString() {
        return "Three of a kind, " + high.normalCasing();
    }

    /**
     * Get the best three of a kind hand possible from list of 7 cards, returning
     * null if no three of a kind hand can not be made with the given list of cards.
     * 
     * @param cards
     * @return
     */
    public static ThreeOfAKind getHand(List<Card> cards) {
	for(Face f : Face.aceToTwo()) {
	    if(cards.stream().filter(c->c.getFace() == f).count() >= 3) {
		List<Face> kickers = cards.stream().map(c->c.getFace()).filter(x -> x != f).sorted((a,b) -> Face.compareByPokerValue(a, b)).collect(Collectors.toList());
		while(kickers.size() > 2)
		    kickers.remove(0);
		return new ThreeOfAKind(f, kickers);
	    }
	}
	return null;
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof ThreeOfAKind) {
	    ThreeOfAKind other = (ThreeOfAKind) o;
	    int x = Face.compareByPokerValue(high, other.high);
	    if (x != 0)
		return x;
	    for (int i = 1; i >= 0; i--) {
		x = Face.compareByPokerValue(kickers.get(i), other.kickers.get(i));
		if (x != 0)
		    return x;
	    }
	    return 0;
	}
	return super.compareTo(o);
    }

}

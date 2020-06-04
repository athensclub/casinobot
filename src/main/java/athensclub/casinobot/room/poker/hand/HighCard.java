package athensclub.casinobot.room.poker.hand;

import java.util.List;
import java.util.stream.Collectors;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;

/**
 * Represent a high card hand
 * 
 * @author Athensclub
 *
 */
public class HighCard extends PokerHand {

    private List<Face> hand;

    public HighCard(List<Face> hand) {
	this.hand = hand;
	level = 2;
    }

    @Override
    public String toString() {
	return hand.get(4).normalCasing() + " high";
    }

    /**
     * Make the best possible high card hand from the given list of 7 cards.
     * 
     * @param cards
     * @return
     */
    public static HighCard getHand(List<Card> cards) {
	List<Face> hand = cards.stream().map(c -> c.getFace()).sorted((a, b) -> Face.compareByPokerValue(a, b))
		.collect(Collectors.toList());
	while (hand.size() > 5)
	    hand.remove(0);
	return new HighCard(hand);
    }

    @Override
    public int compareTo(PokerHand o) {
	if (o instanceof HighCard) {
	    HighCard other = (HighCard) o;
	    for (int i = 4; i >= 0; i--) {
		int x = Face.compareByPokerValue(hand.get(i), other.hand.get(i));
		if (x != 0)
		    return x;
	    }
	    return 0;
	}
	return super.compareTo(o);
    }

}

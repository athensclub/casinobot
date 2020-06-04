package athensclub.casinobot.room.poker.hand;

import java.util.List;

import athensclub.casinobot.card.Card;

/**
 * Represent a poker hand.
 * 
 * @author Athensclub
 *
 */
public class PokerHand implements Comparable<PokerHand> {

    protected int level;

    /**
     * Take in 7 cards and return the best possible hand that can be created from
     * the given cards.
     * 
     * @param cards
     * @return
     */
    public static PokerHand getBestHand(List<Card> cards) {
	PokerHand result = null;
	if ((result = StraightFlush.getHand(cards)) != null)
	    return result;
	if ((result = FourOfAKind.getHand(cards)) != null)
	    return result;
	if ((result = FullHouse.getHand(cards)) != null)
	    return result;
	if ((result = Flush.getHand(cards)) != null)
	    return result;
	if ((result = Straight.getHand(cards)) != null)
	    return result;
	if ((result = ThreeOfAKind.getHand(cards)) != null)
	    return result;
	if ((result = TwoPair.getHand(cards)) != null)
	    return result;
	if ((result = Pair.getHand(cards)) != null)
	    return result;
	return HighCard.getHand(cards);
    }

    @Override
    public int compareTo(PokerHand o) {
	return Integer.compare(level, o.level);
    }

}

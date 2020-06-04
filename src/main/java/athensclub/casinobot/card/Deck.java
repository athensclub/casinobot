package athensclub.casinobot.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represent a deck of 52 normal playing cards.
 * 
 * @author Athensclub
 *
 */
public class Deck {

    private ArrayList<Card> cards = new ArrayList<>();

    {
	for (Suit s : Suit.values()) {
	    for (Face f : Face.values()) {
		cards.add(new Card(f, s));
	    }
	}
    }

    /**
     * Shuffle this deck
     */
    public void shuffle() {
	Collections.shuffle(cards);
    }

    /**
     * Get the amount of cards left in this deck
     * 
     * @return
     */
    public int remainingCard() {
	return cards.size();
    }

    /**
     * Take a card out of this deck and return it.
     * 
     * @return
     */
    public Card deal() {
	return cards.remove(cards.size() - 1);
    }

    /**
     * Take card up to the number of amount and return it as a list.
     * 
     * @param amt
     * @return
     */
    public List<Card> deal(int amt) {
	List<Card> result = new ArrayList<>();
	for (int i = 0; i < amt; i++)
	    result.add(deal());
	return result;
    }

}

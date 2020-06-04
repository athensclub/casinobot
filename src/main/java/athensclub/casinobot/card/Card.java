package athensclub.casinobot.card;

import java.awt.image.BufferedImage;
import java.util.Objects;

import athensclub.casinobot.card.image.CardImages;

/**
 * Represent a single card.
 * 
 * @author Athensclub
 *
 */
public class Card {

    private Suit suit;

    private Face face;

    public Card(Face face, Suit suit) {
	this.face = face;
	this.suit = suit;
    }

    public Card(String notation) {
	if (notation.length() == 3) {
	    face = Face.parse(notation.substring(0, 2));
	    suit = Suit.parse(notation.charAt(2));
	} else if (notation.length() == 2) {
	    face = Face.parse(notation.substring(0, 1));
	    suit = Suit.parse(notation.charAt(1));
	} else
	    throw new IllegalArgumentException("Invalid card notation: " + notation);
    }

    /**
     * Get the face of this card.
     * 
     * @return
     */
    public Face getFace() {
	return face;
    }

    /**
     * Get the suit of this card.
     * 
     * @return
     */
    public Suit getSuit() {
	return suit;
    }

    /**
     * Get the image representing this card.
     * 
     * @return
     */
    public BufferedImage getImage() {
	return CardImages.get(this);
    }

    @Override
    public int hashCode() {
	return Objects.hash(face, suit);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Card) {
	    Card card = (Card) obj;
	    return card.face == face && card.suit == suit;
	}
	return false;
    }

    @Override
    public String toString() {
	return face.getNotation() + suit.getNotation();
    }

}

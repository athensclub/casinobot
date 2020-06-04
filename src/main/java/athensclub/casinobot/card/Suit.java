package athensclub.casinobot.card;

/**
 * enum representing a card suit.
 * 
 * @author Athensclub
 *
 */
public enum Suit {

    SPADE("S"), HEART("H"), CLUB("C"), DIAMOND("D");

    private String notation;

    private Suit(String not) {
	notation = not;
    }

    /**
     * Get the string notation of this suit (ie. D,S,C,H)
     * 
     * @return
     */
    public String getNotation() {
	return notation;
    }

    /**
     * Get the suit from the given character (S,H,C,D)
     * 
     * @param ch
     * @return
     */
    public static Suit parse(char ch) {
	switch (Character.toLowerCase(ch)) {
	case 's':
	    return SPADE;
	case 'h':
	    return HEART;
	case 'd':
	    return DIAMOND;
	case 'c':
	    return CLUB;
	}
	throw new IllegalArgumentException("Unknown suit: " + ch);
    }

}

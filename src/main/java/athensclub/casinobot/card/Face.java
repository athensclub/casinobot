package athensclub.casinobot.card;

/**
 * Representing a card face
 * 
 * @author Athensclub
 *
 */
public enum Face {

    TWO(0, "2", null), THREE(1, "3", TWO), FOUR(2, "4", THREE), FIVE(3, "5", FOUR), SIX(4, "6", FIVE), SEVEN(5, "7",
	    SIX), EIGHT(6, "8", SEVEN), NINE(7, "9", EIGHT), TEN(8, "10",
		    NINE), JACK(9, "J", TEN), QUEEN(10, "Q", JACK), KING(11, "K", QUEEN), ACE(12, "A", KING);

    private static final Face[] ACETOSIX = { ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX };

    private static final Face[] ACETOFIVE = {ACE,TWO,THREE,FOUR,FIVE};
    
    private static final Face[] ACETOTWO = {ACE,KING,QUEEN,JACK,TEN,NINE,EIGHT,SEVEN,SIX,FIVE,FOUR,THREE,TWO};
    
    /**
     * Return array sorted by poker value from max to min, from ace to two.
     * @return
     */
    public static Face[] aceToTwo() {
	return ACETOTWO;
    }
    
    /**
     * Return array sorted by poker value from max to min, from ace to six.
     * 
     * @return
     */
    public static Face[] aceToSix() {
	return ACETOSIX;
    }
    
    /**
     * All the cards required for a five-high straight
     * @return
     */
    public static Face[] aceToFive() {
	return ACETOFIVE;
    }

    private int comparingValue;

    private String notation;

    private Face previous;

    private Face(int pVal, String not, Face prev) {
	comparingValue = pVal;
	previous = prev;
	notation = not;
    }

    /**
     * Get the face that come before this face in the poker value.
     * 
     * @return
     */
    public Face getPrevious() {
	return previous;
    }

    /**
     * Get the notation of this face (ex. 2,3,A,J,K).
     * 
     * @return
     */
    public String getNotation() {
	return notation;
    }

    /**
     * Return this face string value where the first character is upper-case and the
     * others are lower-case.
     * 
     * @return
     */
    public String normalCasing() {
	String str =toString();
	return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Compare two face of cards, by poker hand strength value.
     * 
     * @param a
     * @param b
     * @return
     */
    public static int compareByPokerValue(Face a, Face b) {
	return Integer.compare(a.comparingValue, b.comparingValue);
    }

    /**
     * Parse the given value into a face (ex. 2,3,A,J,K)
     * 
     * @param ch
     * @return
     */
    public static Face parse(String str) {
	switch (str.toLowerCase()) {
	case "1":
	case "a":
	    return ACE;
	case "2":
	    return TWO;
	case "3":
	    return THREE;
	case "4":
	    return FOUR;
	case "5":
	    return FIVE;
	case "6":
	    return SIX;
	case "7":
	    return SEVEN;
	case "8":
	    return EIGHT;
	case "9":
	    return NINE;
	case "10":
	    return TEN;
	case "j":
	    return JACK;
	case "q":
	    return QUEEN;
	case "k":
	    return KING;
	}
	throw new IllegalArgumentException("Unknown card face: " + str);
    }

}

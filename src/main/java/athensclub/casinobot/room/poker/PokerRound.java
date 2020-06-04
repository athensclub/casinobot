package athensclub.casinobot.room.poker;

public enum PokerRound {

    PREFLOP(3), FLOP(1), TURN(1), RIVER(0);

    private int cardsDeal;

    private PokerRound next;

    static {
	PREFLOP.next = FLOP;
	FLOP.next = TURN;
	TURN.next = RIVER;
    }

    private PokerRound(int deal) {
	cardsDeal = deal;
    }

    /**
     * Get amount of card to deal after this round finished.
     * 
     * @return
     */
    public int getCardsDeal() {
	return cardsDeal;
    }

    /**
     * Get the round after this round.
     * 
     * @return
     */
    public PokerRound getNext() {
	return next;
    }

}

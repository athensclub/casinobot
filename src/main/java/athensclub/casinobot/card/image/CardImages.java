package athensclub.casinobot.card.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import athensclub.casinobot.card.Card;
import athensclub.casinobot.card.Face;
import athensclub.casinobot.card.Suit;

/**
 * A class responsible for cards images.
 * 
 * @author Athensclub
 *
 */
public class CardImages {

    private static final HashMap<String, BufferedImage> images = new HashMap<>();

    private static int cardWidth, cardHeight;

    static {
	for (Face f : Face.values()) {
	    for (Suit s : Suit.values()) {
		String not = f.getNotation() + s.getNotation();
		try {
		    BufferedImage img = ImageIO.read(CardImages.class.getResource(not + ".png"));
		    images.put(not, img);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * Get an image represent the given card.
     * 
     * @param card
     * @return
     */
    public static BufferedImage get(Card card) {
	return images.get(card.toString());
    }

    /**
     * Get an image that is a sequence of cards specified by the given list.
     * 
     * @param cards
     * @return
     */
    public static BufferedImage merge(List<Card> cards) {
	int x = 0;
	BufferedImage result = new BufferedImage(cards.stream().mapToInt(c -> c.getImage().getWidth()).sum(),
		cards.stream().mapToInt(c -> c.getImage().getHeight()).max().getAsInt(), BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = result.createGraphics();
	for (Card c : cards) {
	    g.drawImage(c.getImage(), x, 0, null);
	    x += c.getImage().getWidth();
	}
	g.dispose();
	return result;
    }

}

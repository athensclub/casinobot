package athensclub.casinobot;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

/**
 * An interface that contains default utilities command for discord api.
 * 
 * @author Athensclub
 *
 */
public interface UtilCommands {

    /**
     * Parse a argument in form of -property=value to a java map. This will only
     * parse argument in the fromInclusive to toInclusive index of the given
     * arguments list. This will convert every key to lower case.
     * 
     * @param args
     * @param fromInclusive
     * @param toInclusive
     * @return
     */
    public default Map<String, String> parseArgs(List<String> args, int fromInclusive, int toInclusive) {
	Map<String, String> result = new HashMap<>();
	for (int i = fromInclusive; i <= toInclusive; i++) {
	    String str = args.get(i);
	    if (str.startsWith("-")) {
		int idx = str.indexOf('=');
		if (idx > 0) {
		    result.put(str.substring(1, idx).toLowerCase(), str.substring(idx + 1));
		} else
		    throw new IllegalArgumentException("Argument without value: " + str);
	    } else
		throw new IllegalArgumentException("Unknowna argument: " + str);
	}
	return result;
    }

    /**
     * Parse a argument in form of -property=value to a java map. This will only
     * parse argument in the fromInclusive to last index of the given arguments
     * list. This will convert every key to lower case.
     * 
     * @param args
     * @param fromInclusive
     * @param toInclusive
     * @return
     */
    public default Map<String, String> parseArgs(List<String> args, int fromInclusive) {
	return parseArgs(args, fromInclusive, args.size() - 1);
    }

    /**
     * Get id from the given metion tag.
     * 
     * @param mentionTag
     * @return
     */
    public default long id(String mentionTag) {
	return Long.parseLong(mentionTag.substring(3, mentionTag.length() - 1));
    }

    /**
     * Create an error message ready to be sent
     * 
     * @param str
     * @return
     */
    public default MessageBuilder error(String str) {
	return new MessageBuilder()
		.setEmbed(new EmbedBuilder().setColor(Color.RED).setTitle("ERROR").setDescription(str));
    }

    /**
     * Create a info message with given title and message ready to be sent.
     * 
     * @param title
     * @param msg
     * @return
     */
    public default MessageBuilder info(String title, String msg) {
	return new MessageBuilder()
		.setEmbed(new EmbedBuilder().setColor(Color.GREEN).setTitle(title).setDescription(msg));
    }

}

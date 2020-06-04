package athensclub.casinobot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * A command manager that handle command with prefix '$'
 * 
 * @author Athensclub
 *
 */
public class SimpleCommandManager implements CommandManager {

    private HashMap<String, BiConsumer<MessageCreateEvent, List<String>>> commands = new HashMap<>();

    private HashMap<String, String[]> usableRoles = new HashMap<>();

    private DiscordApi api;

    /**
     * Add the given command into this manager's dictionary.
     * 
     * @param commandName
     * @param action
     */
    public void addCommand(String commandName, BiConsumer<MessageCreateEvent, List<String>> action) {
	commands.put(commandName, action);
    }

    /**
     * Make the given command be usable only for the given roles.If a user uses a
     * command without a specified role, a message error will appear and the command
     * will not run.
     * 
     * @param commandName
     * @param usableRole
     */
    public void setRoleRequirements(String commandName, String... usableRole) {
	usableRoles.put(commandName, usableRole);
    }

    @Override
    public DiscordApi getApi() {
	return api;
    }

    @Override
    public void setApi(DiscordApi api) {
	this.api = api;
    }

    @Override
    public boolean isValid(String expr,MessageCreateEvent e) {
	if (!expr.contains(" "))
	    return commands.containsKey(expr);
	return commands.containsKey(expr.substring(0, expr.indexOf(' ')));
    }

    @Override
    public void run(String expr, MessageCreateEvent e) {
	User user = e.getMessageAuthor().asUser().get();
	List<String> result = new ArrayList<>();
	StringBuilder temp = new StringBuilder();
	String command = expr;
	if (expr.contains(" ")) {
	    command = expr.substring(0, expr.indexOf(' '));
	    for (int i = command.length(); i < expr.length(); i++) {
		char c = expr.charAt(i);
		if (Character.isWhitespace(c)) {
		    if (temp.length() == 0)
			continue;
		    result.add(temp.toString());
		    temp = new StringBuilder();
		} else {
		    temp.append(c);
		}
	    }
	    if (temp.length() > 0)
		result.add(temp.toString());
	}
	try {
	    String[] roles = usableRoles.get(command);
	    if (roles != null) {
		if (!user.getRoles(e.getServer().get()).stream()
			.anyMatch(r -> Arrays.stream(roles).anyMatch(x -> r.getName().equalsIgnoreCase(x)))) {
		    error(user.getNicknameMentionTag() + " must have role " + String.join(" or ", roles)
			    + " to use command ").send(e.getChannel());
		    return;
		}
	    }
	    commands.get(command).accept(e, result);
	} catch (Exception ex) {
	    error(user.getNicknameMentionTag() + " used command " + command + " with invalid expression:\n"
		    + ex.getMessage()).send(e.getChannel());
	    ex.printStackTrace();
	}
    }

}

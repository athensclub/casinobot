package athensclub.casinobot;

import java.awt.Color;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * A interface responsible for commands handling.This will also contain default
 * utility methods.
 * 
 * @author Athensclub
 *
 */
public interface CommandManager extends UtilCommands {

    /**
     * Set the api used by this manager
     * 
     * @param api
     */
    public void setApi(DiscordApi api);

    /**
     * Get the api used by this manager.
     * 
     * @return
     */
    public DiscordApi getApi();

    /**
     * Check whether the given command is valid for this manager.
     * 
     * @param expr
     * @return
     */
    public boolean isValid(String expr, MessageCreateEvent e);

    /**
     * Evaluate the given command.
     * 
     * @param expr
     * @return
     */
    public void run(String expr, MessageCreateEvent e);

}

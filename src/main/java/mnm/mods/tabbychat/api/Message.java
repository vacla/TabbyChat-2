package mnm.mods.tabbychat.api;

import java.time.LocalDateTime;

import net.minecraft.text.Text;

/**
 * Represents a message.
 */
public interface Message {

    /**
     * Gets the message
     *
     * @return The message
     */
    Text getMessage();

    /**
     * Gets the date that this message was sent.
     *
     * @return The date
     */
    LocalDateTime getDateTime();

}

package mnm.mods.tabbychat.api.filters;

import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;

import java.util.regex.Pattern;

/**
 * A filter is used to filter chat.
 */
public interface Filter {

    /**
     * Gets the pattern that will trigger this filter.
     *
     * @return The pattern
     */
    Pattern getPattern();

    void action(FilterEvent event);

    /**
     * Used to convert the component to the string.
     * <p>Default implementation also strips any control/color codes.</p>
     *
     * @param string The text component to be processed
     * @return The string which will be used for the
     */
    default String prepareText(Text string) {
        return ChatUtil.stripTextFormat(string.getString());
    }

}

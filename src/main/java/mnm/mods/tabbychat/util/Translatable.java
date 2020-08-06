package mnm.mods.tabbychat.util;

import net.minecraft.client.resource.language.I18n;

/**
 * Translatable strings. Translations are done via
 * {@link I18n#translate(String, Object...)}.
 *
 * @author Matthew
 */
public interface Translatable {

    /**
     * Gets the unlocalized string for this translation
     *
     * @return The untranslated string
     */
    String getUnlocalized();

    /**
     * Translates this string.
     *
     * </pre>
     *
     * @param params Translation parameters
     * @return The translated string
     */
    default String translate(Object... params) {
        return I18n.translate(getUnlocalized(), params);
    }
}

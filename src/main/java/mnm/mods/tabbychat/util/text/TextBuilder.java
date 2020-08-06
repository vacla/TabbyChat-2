package mnm.mods.tabbychat.util.text;

import net.minecraft.text.MutableText;

import javax.annotation.Nullable;

public class TextBuilder extends AbstractChatBuilder {

    private MutableText chat;

    /**
     * Used for builders that build multiple chats.
     *
     * @return This builder
     */
    @Override
    public ITextBuilder next() {
        throw new UnsupportedOperationException();
    }

    /**
     * Ends a translation so it can be appended to the chat.
     *
     * @return
     */
    @Override
    public ITextBuilder end() {
        throw new UnsupportedOperationException();
    }

    /**
     * Appends the current chat to the chat and makes the provided value the
     * current.
     *
     * @param chat The new current value
     * @return
     */
    @Override
    public TextBuilder append(@Nullable MutableText chat) {

        if (current != null) {
            if (this.chat == null)
                this.chat = current;
            else
                this.chat.append(current);
        }
        current = chat;
        return this;
    }

    /**
     * Appends the current chat (if any) to the chat and returns the built chat.
     *
     * @return The chat
     */
    @Override
    public MutableText build() {
        return append(null).chat;
    }
}

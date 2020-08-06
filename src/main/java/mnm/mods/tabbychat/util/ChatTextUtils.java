package mnm.mods.tabbychat.util;

import com.google.common.collect.Lists;
import mnm.mods.tabbychat.api.Message;
import mnm.mods.tabbychat.client.ChatMessage;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.settings.GeneralSettings;
import mnm.mods.tabbychat.util.text.TextBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ChatTextUtils {

    public static List<StringRenderable> split(Text chat, int width) {
        TextRenderer fr = MinecraftClient.getInstance().textRenderer;
        return ChatMessages.breakRenderedChatMessageLines(chat, width, fr);
    }

    public static List<ChatMessage> split(List<ChatMessage> list, int width) {
        if (width <= 8) // ignore, characters are larger than width
            return Lists.newArrayList(list);
        // prevent concurrent modification caused by chat thread
        synchronized (list) {
            List<ChatMessage> result = Lists.newArrayList();
            Iterator<ChatMessage> iter = list.iterator();
            while (iter.hasNext() && result.size() <= 100) {
                ChatMessage line = iter.next();
                List<StringRenderable> chatlist = split(getMessageWithOptionalTimestamp(line), width);
                for (int i = chatlist.size() - 1; i >= 0; i--) {
                    StringRenderable chat = chatlist.get(i);
                    result.add(new ChatMessage(line.getCounter(), new LiteralText(chat.getString()), line.getID(), false));
                }
            }
            return result;
        }
    }

    public static Text getMessageWithOptionalTimestamp(Message msg) {
        GeneralSettings settings = TabbyChatClient.getInstance().getSettings().general;
        if (msg.getDateTime() != null && settings.timestampChat.get()) {

            TimeStamps stamp = settings.timestampStyle.get();
            Formatting format = settings.timestampColor.get();
            return new TextBuilder().text("")
                    .text(stamp.format(msg.getDateTime()) + " ").format(format)
                    .append(msg.getMessage())
                    .build();
        }
        return msg.getMessage();

    }

    /**
     * Returns a ChatComponent that is a sub-component of another one. It begins
     * at the specified index and extends to the end of the componenent.
     *
     * @param chat       The chat to subchat
     * @param beginIndex The beginning index, inclusive
     * @return The end of the chat
     * @see String#substring(int)
     */
    public static MutableText subChat(Text chat, int beginIndex) {
        final MutableText[] rchat = {null};
        final int[] pos = {0};
        chat.visit((style, text) -> {
            MutableText part = new LiteralText(text).setStyle(style);
            int len = text.length();
            if (len + pos[0] >= beginIndex) {
                if (pos[0] < beginIndex) {
                    MutableText schat = new LiteralText(text.substring(beginIndex - pos[0]));
                    schat.setStyle(style);
                    part = schat;
                }
                if (rchat[0] == null) {
                    rchat[0] = part;
                } else {
                    rchat[0].append(part);
                }
            }
            pos[0] += len;
            return Optional.empty();
        }, Style.EMPTY);
        return rchat[0];
    }
}

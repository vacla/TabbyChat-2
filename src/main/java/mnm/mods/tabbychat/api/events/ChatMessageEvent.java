package mnm.mods.tabbychat.api.events;

import com.google.common.collect.Sets;
import mnm.mods.tabbychat.api.Channel;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
//import net.minecraftforge.eventbus.api.Cancelable;
//import net.minecraftforge.eventbus.api.Event;

import java.util.Set;

public abstract class ChatMessageEvent /*extends Event*/ {

    /**
     * Used to listen to chat and modify it. Can also select which channels it
     * goes to.
     */
    //@Cancelable
    public static class ChatReceivedEvent extends ChatMessageEvent {

        public MutableText text;
        public int id;
        public Set<Channel> channels = Sets.newHashSet();

        public ChatReceivedEvent(MutableText text, int id) {
            this.text = text;
            this.id = id;
        }
    }
}

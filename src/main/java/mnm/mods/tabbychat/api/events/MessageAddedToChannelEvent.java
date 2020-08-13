package mnm.mods.tabbychat.api.events;

import mnm.mods.tabbychat.api.Channel;
import net.minecraft.text.MutableText;

public abstract class MessageAddedToChannelEvent /*extends Event*/ {

    protected MutableText text;
    protected int id;
    private final Channel channel;

    protected MessageAddedToChannelEvent(MutableText text, int id, Channel channel) {
        this.text = text;
        this.id = id;
        this.channel = channel;
    }

    public MutableText getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    //@Cancelable
    public static class Pre extends MessageAddedToChannelEvent {

        public Pre(MutableText text, int id, Channel channel) {
            super(text, id, channel);
        }

        public void setText(MutableText text) {
            this.text = text;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Post extends MessageAddedToChannelEvent {

        public Post(MutableText text, int id, Channel channel) {
            super(text, id, channel);
        }
    }
}

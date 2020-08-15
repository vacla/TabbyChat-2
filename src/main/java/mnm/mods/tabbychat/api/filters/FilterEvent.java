package mnm.mods.tabbychat.api.filters;

import java.util.Set;
import java.util.regex.Matcher;

import mnm.mods.tabbychat.api.Channel;
import net.minecraft.text.MutableText;

public class FilterEvent {

    public final Matcher matcher;
    public MutableText text;
    public Set<Channel> channels;

    public FilterEvent(Matcher matcher, Set<Channel> channels, MutableText text) {
        this.matcher = matcher;
        this.text = text;
        this.channels = channels;
    }
}

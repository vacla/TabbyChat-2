package mnm.mods.tabbychat.api.filters;

import java.util.Set;
import java.util.regex.Matcher;

import mnm.mods.tabbychat.api.Channel;
import net.minecraft.text.Text;

public class FilterEvent {

    public final Matcher matcher;
    public Text text;
    public Set<Channel> channels;

    public FilterEvent(Matcher matcher, Set<Channel> channels, Text text) {
        this.matcher = matcher;
        this.text = text;
        this.channels = channels;
    }
}

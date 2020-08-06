package mnm.mods.tabbychat.util.text;

import com.google.common.collect.Streams;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.stream.Stream;

public class FancyTextComponent extends BaseText
{

    private final Text text;
    private FancyTextStyle style;

    public FancyTextComponent(MutableText parent) {
        if (parent instanceof FancyTextComponent)
            throw new IllegalArgumentException("Parent text cannot be fancy");
        this.text = parent;
    }

    @Override
    public String getRawString() {
        return text.getString();
    }

    @Override
    public LiteralText copy() {
        Text text = this.text.shallowCopy();
        FancyTextComponent fcc = new FancyTextComponent(text);
        fcc.setFancyStyle(getFancyStyle().createCopy());
        return fcc;
    }

    @Override
    public Iterator<Text> iterator() {
        // don't iterate using the vanilla components
        return Streams.stream(this.text.iterator())
                .map(it -> it instanceof FancyTextComponent ? it
                        : new FancyTextComponent(it).setFancyStyle(this.getFancyStyle())).iterator();
    }

    @Override
    public Stream<Text> stream() {
        return Streams.concat(Stream.of(this), getSiblings().stream().flatMap(Text::stream));
    }

    public Text getText() {
        return text;
    }

    public FancyTextStyle getFancyStyle() {
        if (this.style == null)
            this.style = new FancyTextStyle();
        return this.style;
    }

    public FancyTextComponent setFancyStyle(FancyTextStyle style) {
        this.style = style;
        return this;
    }

    @Override
    public String toString() {
        return String.format("FancyText{text=%s, fancystyle=%s}", text, style);
    }
}

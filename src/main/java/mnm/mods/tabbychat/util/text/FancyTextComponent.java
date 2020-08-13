package mnm.mods.tabbychat.util.text;

import net.minecraft.text.*;

import java.util.Optional;

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
    public String getString() {
        return text.getString();
    }

    @Override
    public BaseText copy()
    {
        MutableText text = this.text.shallowCopy();
        FancyTextComponent fcc = new FancyTextComponent(text);
        fcc.setFancyStyle(getFancyStyle().createCopy());
        return fcc;
    }

    @Override
    public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
        // don't iterate using the vanilla components
        return this.text.visit((style, text) -> {
            new FancyTextComponent(new LiteralText(text)).setFancyStyle(this.getFancyStyle());
            return Optional.empty();
        }, Style.EMPTY);
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

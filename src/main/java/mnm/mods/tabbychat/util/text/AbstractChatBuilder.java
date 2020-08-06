package mnm.mods.tabbychat.util.text;

import mnm.mods.tabbychat.util.Color;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public abstract class AbstractChatBuilder implements ITextBuilder {

    protected MutableText current;

    @Override
    public ITextBuilder format(Formatting f) {
        checkCreated();
        if (f.isColor()) {
            current.getStyle().withColor(f);
        } else if (f.isModifier()) {
            if (f == Formatting.BOLD) {
                current.getStyle().withBold(true);
            } else if (f == Formatting.ITALIC) {
                current.getStyle().withItalic(true);
            } else if (f == Formatting.UNDERLINE) {
                current.getStyle().withFormatting(Formatting.UNDERLINE);
            } else if (f == Formatting.STRIKETHROUGH) {
                current.getStyle().withFormatting(Formatting.STRIKETHROUGH);
            } else if (f == Formatting.OBFUSCATED) {
                current.getStyle().withFormatting(Formatting.OBFUSCATED);
            }
        } else if (f == Formatting.RESET) {
            current.getStyle().withParent(Style.EMPTY);
        }
        return this;
    }

    @Override
    public ITextBuilder color(Color color) {
        asFancy().getFancyStyle().setColor(color);
        return this;
    }

    @Override
    public ITextBuilder underline(Color color) {
        asFancy().getFancyStyle().setUnderline(color);
        return this;
    }

    @Override
    public ITextBuilder highlight(Color color) {
        asFancy().getFancyStyle().setHighlight(color);
        return this;
    }

    private FancyTextComponent asFancy() {
        if (!(current instanceof FancyTextComponent)) {
            current = new FancyTextComponent(current);
        }
        return (FancyTextComponent) current;
    }

    @Override
    public ITextBuilder click(ClickEvent event) {
        checkCreated();
        current.getStyle().withClickEvent(event);
        return this;
    }

    @Override
    public ITextBuilder hover(HoverEvent event) {
        checkCreated();
        current.getStyle().setHoverEvent(event);
        return this;
    }

    @Override
    public ITextBuilder insertion(String insertion) {
        checkCreated();
        current.getStyle().withInsertion(insertion);
        return this;
    }

    private void checkCreated() {
        if (current == null) {
            throw new IllegalStateException("A chat component has not been created yet.");
        }
    }

    @Override
    public ITextBuilder score(String player, String objective) {
        return append(new ScoreText(player, objective));
    }

    @Override
    public ITextBuilder text(String text) {
        return append(new LiteralText(text));
    }

    @Override
    public ITextBuilder selector(Selector selector) {
        return append(new SelectorText(selector.toString()));
    }

    @Override
    public ITextBuilder translation(String key) {
        return new TranslationBuilder(this, key);
    }

    @Override
    public ITextBuilder quickTranslate(String key) {
        return translation(key).end();
    }

}

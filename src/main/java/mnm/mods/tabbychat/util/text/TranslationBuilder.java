package mnm.mods.tabbychat.util.text;

import com.google.common.collect.Lists;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

import java.util.List;
import javax.annotation.Nullable;

class TranslationBuilder extends AbstractChatBuilder {

    private final ITextBuilder parent;
    private final String translationKey;
    private List<Object> translationArgs = Lists.newArrayList();

    private MutableText buffer;

    TranslationBuilder(ITextBuilder parent, String key) {
        this.parent = parent;
        this.translationKey = key;
    }

    @Override
    public ITextBuilder next() {
        translationArgs.add(append(null).buffer);
        buffer = null;
        return this;
    }

    @Override
    public ITextBuilder end() {
        MutableText buffer = append(null).buffer;
        if (buffer != null)
            translationArgs.add(buffer);
        return parent.append(new TranslatableText(translationKey, translationArgs.toArray()));
    }

    @Override
    public MutableText build() {
        throw new IllegalStateException("Translation in progress.");
    }

    @Override
    public TranslationBuilder append(@Nullable MutableText chat) {
        if (current != null) {
            if (this.buffer == null)
                buffer = current;
            else
                this.buffer.append(current);
        }
        current = chat;
        return this;
    }
}

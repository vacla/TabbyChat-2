package mnm.mods.tabbychat.mixin;

import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.util.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestor.class)
public interface MixinCommandSuggestor
{
    @Accessor("width")
    int getWidth();

    @Accessor("messages")
    List<String> getMessages();

    @Accessor("window")
    CommandSuggestor.SuggestionWindow getWindow();

    @Invoker
    String invokeProvideRenderText(String original, int firstCharacterIndex);

    @Mixin(CommandSuggestor.SuggestionWindow.class)
    interface MixinSuggestionWindow
    {
        @Accessor("area")
        void setArea(Rect2i area);

        @Accessor("area")
        Rect2i getArea();
    }
}

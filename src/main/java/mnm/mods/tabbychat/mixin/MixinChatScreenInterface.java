package mnm.mods.tabbychat.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatScreen.class)
public interface MixinChatScreenInterface
{
    @Accessor("field_2389")
    String getChatFieldText();

    @Accessor("field_2389")
    void setChatFieldText(String text);

    @Accessor("chatField")
    void setChatField(TextFieldWidget chatField);

    @Accessor("commandSuggestor")
    CommandSuggestor getCommandSuggestor();

    @Invoker
    void invokeOnChatFieldUpdate(String chatText);
}

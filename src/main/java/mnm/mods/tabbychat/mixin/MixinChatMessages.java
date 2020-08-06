package mnm.mods.tabbychat.mixin;

import net.minecraft.client.util.ChatMessages;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatMessages.class)
public interface MixinChatMessages
{
    @Invoker
    static String invokeGetRenderedChatMessage(String string)
    {
        return null;
    }
}

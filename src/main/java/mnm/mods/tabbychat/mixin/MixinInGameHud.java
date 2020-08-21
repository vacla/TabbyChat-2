package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.gui.GuiNewChatTC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class MixinInGameHud
{
    @Shadow
    @Final
    @Mutable
    private ChatHud chatHud;

    @Redirect(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;chatHud:Lnet/minecraft/client/gui/hud/ChatHud;"))
    private void setChatHud(InGameHud inGameHud, ChatHud chatHud)
    {
        this.chatHud = new GuiNewChatTC(MinecraftClient.getInstance(), TabbyChatClient.getInstance());
    }
}

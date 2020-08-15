package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.core.GuiChatTC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;scanPacks()V", shift = At.Shift.BEFORE))
    private void setOnLoadFinished(RunArgs args, CallbackInfo ci)
    {
        TabbyChatClient.onLoadingFinished();
    }
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onClientTick(CallbackInfo ci)
    {
        GuiChatTC.onTick();
    }

    @Inject(method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", shift = At.Shift.BEFORE))
    private void openScreen(Screen screen, CallbackInfo ci)
    {
        TabbyChatClient.NullScreenListener.onGuiOpen(screen);
    }
}

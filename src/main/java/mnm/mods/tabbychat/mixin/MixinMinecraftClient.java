package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.gui.GuiChatTC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Redirect(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;registerListener(Lnet/minecraft/resource/ResourceReloadListener;)V", ordinal = 16))
    private void setOnLoadFinished(ReloadableResourceManager reloadableResourceManager, ResourceReloadListener listener)
    {
        reloadableResourceManager.registerListener(listener);
        TabbyChatClient.onLoadingFinished();
    }
    /*@Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;registerListener(Lnet/minecraft/resource/ResourceReloadListener;)V", ordinal = 0))
    private void setOnLoadFinished(RunArgs args, CallbackInfo ci)
    {
        TabbyChatClient.onLoadingFinished();
    }*/
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

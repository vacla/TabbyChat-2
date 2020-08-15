package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.core.GuiChatTC;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onClientTick(CallbackInfo ci)
    {
        GuiChatTC.onTick();
    }
}

package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.core.GuiChatTC;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    @Redirect(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void renderChat(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if(GuiChatTC.onRenderChat(matrices, screen, mouseX, mouseY, delta))
        {
            screen.render(matrices, mouseX, mouseY, delta);
        }
    }
}

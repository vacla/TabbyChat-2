package mnm.mods.tabbychat.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public interface MixinScreen
{
    @Invoker
    void invokeRenderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int i, int j);
}

package mnm.mods.tabbychat.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextFieldWidget.class)
public interface MixinTextFieldWidget
{
    @Invoker
    int invokeGetMaxLength();

    @Invoker
    void invokeDrawSelectionHighlight(int x1, int y1, int x2, int y2);
}

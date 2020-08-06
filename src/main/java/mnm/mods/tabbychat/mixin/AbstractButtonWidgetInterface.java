package mnm.mods.tabbychat.mixin;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractButtonWidget.class)
public interface AbstractButtonWidgetInterface
{
    @Accessor("height")
    void setHeight(int height);
}

package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.gui.GuiChatTC;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MixinMouse
{
    boolean[] boolMouseReleased;
    boolean[] boolMouseClicked;
    @Redirect(method = "onMouseScroll(JDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"))
    private boolean onMouseScroll(Screen parentElement, double mouseX, double mouseY, double amount)
    {
        if(GuiChatTC.onMouseScrolled(parentElement, mouseX, mouseY, amount))
        {
            return true;
        }
        return parentElement.mouseScrolled(mouseX, mouseY, amount);
    }

    @Redirect(method = "method_1602(Lnet/minecraft/client/gui/Element;DDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;mouseDragged(DDIDD)Z"))
    private boolean onMouseDragged(Element element, double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(GuiChatTC.onMouseDragged(element, mouseX, mouseY, button, deltaX, deltaY))
        {
            return true;
        }
        return element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Inject(method = "method_1605([ZDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setBoolMouseReleased(boolean[] bool, double mouseX, double mouseY, int button, CallbackInfo ci)
    {
        this.boolMouseReleased = bool;
    }

    @Redirect(method = "method_1605([ZDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"))
    private boolean onMouseReleased(Screen parentElement, double mouseX, double mouseY, int button)
    {
        boolMouseReleased[0] = GuiChatTC.onMouseReleased(parentElement, mouseX, mouseY, button);
        if (!boolMouseReleased[0]) boolMouseReleased[0] = parentElement.mouseReleased(mouseX, mouseY, button);
        return boolMouseReleased[0];
    }

    @Inject(method = "method_1611([ZDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setBoolMouseClicked(boolean[] bool, double mouseX, double mouseY, int button, CallbackInfo ci)
    {
        this.boolMouseClicked = bool;
    }

    @Redirect(method = "method_1611([ZDDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"))
    private boolean onMouseClicked(Screen parentElement, double mouseX, double mouseY, int button)
    {
        boolMouseClicked[0] = GuiChatTC.onMouseClicked(parentElement, mouseX, mouseY, button);
        if (!boolMouseClicked[0]) boolMouseClicked[0] = parentElement.mouseClicked(mouseX, mouseY, button);
        return boolMouseClicked[0];
    }
}

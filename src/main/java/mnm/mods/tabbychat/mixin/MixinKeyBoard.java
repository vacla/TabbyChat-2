package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.core.GuiChatTC;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Keyboard.class)
public class MixinKeyBoard
{
    boolean[] boolKeyPressed;
    @Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setBoolKeyPressed(long window, int key, int scancode, int i, int j, CallbackInfo ci, boolean[] bool)
    {
        this.boolKeyPressed = bool;
    }

    @Redirect(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Screen;keyReleased(III)Z", shift = At.Shift.BEFORE))
    private boolean onKeyReleased(Screen parentElement, int keyCode, int scanCode, int modifiers)
    {
        boolKeyPressed[0] = GuiChatTC.onKeyReleased(parentElement, keyCode, scanCode, modifiers);
        if (!boolKeyPressed[0]) boolKeyPressed[0] = parentElement.keyReleased(keyCode, scanCode, modifiers);
        return boolKeyPressed[0];
    }

    @Redirect(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Screen;keyPressed(III)Z", shift = At.Shift.BEFORE))
    private boolean onKeyPressed(Screen parentElement, int keyCode, int scanCode, int modifiers)
    {
        boolKeyPressed[0] = GuiChatTC.onKeyPressed(parentElement, keyCode, scanCode, modifiers);
        if (!boolKeyPressed[0]) boolKeyPressed[0] = parentElement.keyPressed(keyCode, scanCode, modifiers);
        return boolKeyPressed[0];
    }

    @Redirect(method = "method_1458(Lnet/minecraft/client/gui/Element;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z", shift = At.Shift.BEFORE))
    private boolean onCharTyped1(Element parentElement, char chr, int keyCode)
    {
        if (GuiChatTC.onCharTyped(parentElement, chr, keyCode))
        {
            return true;
        }
        return parentElement.charTyped(chr, keyCode);
    }

    @Redirect(method = "method_1473(Lnet/minecraft/client/gui/Element;CI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z", shift = At.Shift.BEFORE))
    private boolean onCharTyped2(Element parentElement, char chr, int keyCode)
    {
        if (GuiChatTC.onCharTyped(parentElement, chr, keyCode))
        {
            return true;
        }
        return parentElement.charTyped(chr, keyCode);
    }
}

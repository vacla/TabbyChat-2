package mnm.mods.tabbychat.mixin;

import mnm.mods.tabbychat.client.core.GuiChatTC;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Keyboard.class)
public class MixinKeyBoard
{
    boolean[] bool;
    @Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void something(long window, int key, int scancode, int i, int j, CallbackInfo ci, boolean[] bool)
    {
        this.bool = bool;
    }

    @Redirect(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z", shift = At.Shift.BEFORE))
    private boolean somethingelse(ParentElement parentElement, int keyCode, int scanCode, int modifiers)
    {
        bool[0] = GuiChatTC.onKeyPressed(parentElement, keyCode, scanCode, modifiers);
        if (!bool[0]) bool[0] = parentElement.keyPressed(keyCode, scanCode, modifiers);
        return bool[0];
    }
}

package mnm.mods.tabbychat.client.core;

import mnm.mods.tabbychat.client.AbstractChannel;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.gui.ChatBox;
import mnm.mods.tabbychat.client.gui.component.GuiText;
import mnm.mods.tabbychat.mixin.MixinChatScreenInterface;
import mnm.mods.tabbychat.mixin.MixinCommandSuggestor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ChatScreen;
/*import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;*/
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GuiChatTC {

    private static ChatBox chat;
    public GuiChatTC(ChatBox chat) {
        GuiChatTC.chat = chat;
    }

    @SuppressWarnings("unchecked")
    public static void initGui(MinecraftClient client) {
        if (client.currentScreen instanceof ChatScreen) {
            ChatScreen guichat = (ChatScreen) client.currentScreen;
            AbstractChannel chan = chat.getActiveChannel();
            if (((MixinChatScreenInterface)guichat).getChatFieldText().isEmpty()
                    && !chan.isPrefixHidden()
                    && !chan.getPrefix().isEmpty()) {
                ((MixinChatScreenInterface)guichat).setChatFieldText(chan.getPrefix() + " ");
            }
            GuiText text = chat.getChatInput().getTextField();
            ((MixinChatScreenInterface) guichat).setChatField(text.getTextField());
            text.setValue(((MixinChatScreenInterface) guichat).getChatFieldText());

            chat.getChatInput().setTextFormatter(((MixinCommandSuggestor)((MixinChatScreenInterface) guichat).getCommandSuggestor())::invokeProvideRenderText);
            text.getTextField().setChangedListener(((MixinChatScreenInterface) guichat)::invokeOnChatFieldUpdate);

            List<Element> children = (List<Element>) guichat.children();
            children.set(0, chat);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen && event.phase == TickEvent.Phase.END) {
            chat.tick();
        }
    }

    public static boolean onRenderChat(MatrixStack matrixStack, Screen screen, int mouseX, int mouseY, float tickDelta) {
        if (screen instanceof ChatScreen) {
            chat.update((ChatScreen) screen);
            chat.render(matrixStack, mouseX, mouseY, tickDelta);
            return false;
        }
        return true;
    }

    public static boolean onKeyPressed(ParentElement screen, int keyCode, int scanCode, int modifiers) {
        if (screen instanceof ChatScreen) {
            if (keyPressed((ChatScreen) screen, keyCode)
                    || chat.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onKeyReleased(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCharTyped(GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.charTyped(event.getCodePoint(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onMouseDragged(GuiScreenEvent.MouseDragEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY())) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public void onMouseScrolled(GuiScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getGui() instanceof ChatScreen) {
            if (this.chat.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta())) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean keyPressed(ChatScreen guichat, int key) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            MinecraftClient.getInstance().inGameHud.getChatHud().resetScroll();
            GuiText text = chat.getChatInput().getTextField();
            guichat.sendMessage(text.getValue());
            text.setValue(((MixinChatScreenInterface)guichat).getChatFieldText());

            if (!TabbyChatClient.getInstance().getSettings().advanced.keepChatOpen.get()) {
                MinecraftClient.getInstance().openScreen(null);
            }
            return true;
        }
        return false;
    }
}

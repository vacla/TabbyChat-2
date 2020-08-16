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

    public static void onTick() {
        if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
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
            return keyPressed((ChatScreen) screen, keyCode) || chat.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public static boolean onKeyReleased(ParentElement parentElement, int keyCode, int scanCode, int modifiers) {
        if (parentElement instanceof ChatScreen) {
            return chat.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    public static boolean onCharTyped(Element parentElement, char chr, int keyCode) {
        if (parentElement instanceof ChatScreen) {
            return chat.charTyped(chr, keyCode);
        }
        return false;
    }

    public static boolean onMouseClicked(Screen parentElement, double mouseX, double mouseY, int button) {
        if (parentElement instanceof ChatScreen) {
            return chat.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    public static boolean onMouseReleased(Screen parentElement, double mouseX, double mouseY, int button) {
        if (parentElement instanceof ChatScreen) {
            return chat.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    public static boolean onMouseDragged(Element element, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (element instanceof ChatScreen) {
            return chat.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    public static boolean onMouseScrolled(Screen guichat, double mouseX, double mouseY, double amount) {
        if (guichat instanceof ChatScreen) {
            return chat.mouseScrolled(mouseX, mouseY, amount);
        }
        return false;
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

package mnm.mods.tabbychat.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.client.ChatManager;
import mnm.mods.tabbychat.client.TabbyChatClient;
import mnm.mods.tabbychat.client.extra.spell.Spellcheck;
import mnm.mods.tabbychat.client.extra.spell.SpellingFormatter;
import mnm.mods.tabbychat.client.gui.component.GuiComponent;
import mnm.mods.tabbychat.client.gui.component.GuiPanel;
import mnm.mods.tabbychat.client.gui.component.GuiText;
import mnm.mods.tabbychat.client.gui.component.IGuiEventListenerDelegate;
import mnm.mods.tabbychat.mixin.MixinTextFieldWidget;
import mnm.mods.tabbychat.util.Color;
import mnm.mods.tabbychat.util.Dim;
import mnm.mods.tabbychat.util.ILocation;
import mnm.mods.tabbychat.util.TexturedModal;
import mnm.mods.tabbychat.util.text.FancyFontRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TextBox extends GuiComponent implements IGuiEventListenerDelegate {

    private static final TexturedModal MODAL = new TexturedModal(ChatBox.GUI_LOCATION, 0, 219, 254, 37);

    private TextRenderer fr = mc.textRenderer;
    // Dummy textField
    private GuiText textField = new GuiText(new TextFieldWidget(fr, 0, 0, 0, 0, new LiteralText("input")) {
        @Override
        public void render(MatrixStack matrixStack, int x, int y, float parTicks) {
            // noop
        }

        @Override
        public void setSuggestion(@Nullable String p_195612_1_) {
            suggestion = p_195612_1_;
            super.setSuggestion(p_195612_1_);
        }
    });
    private int cursorCounter;
    //private Spellcheck spellcheck;

    private BiFunction<String, Integer, String> textFormatter = (text, offset) -> text;
    private String suggestion;

    TextBox() {
        //this.spellcheck = TabbyChatClient.getSpellcheck();
        //TabbyChat.logger.warn("spellcheck: " + spellcheck);
        textField.getTextField().setMaxLength(ChatManager.MAX_CHAT_LENGTH);
        textField.getTextField().setFocusUnlocked(false);
        textField.getTextField().setHasBorder(false);
        textField.getTextField().setSelected(true);
    }

    @Override
    public Element delegate() {
        return textField;
    }

    @Override
    public void onClosed() {
        this.textField.setValue("");
        super.onClosed();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float parTicks) {
        RenderSystem.enableBlend();
        drawModalCorners(MODAL);
        RenderSystem.disableBlend();

        drawText(matrixStack);
        drawCursor(matrixStack);
    }

    private void drawCursor(MatrixStack matrixStack) {
        TextFieldWidget textField = this.textField.getTextField();

        // keeps track of all the characters. Used to compensate for spaces
        int totalPos = 0;

        // The current pixel row. adds FONT_HEIGHT each iteration
        int line = 0;

        // The position of the cursor
        int pos = textField.getCursor();
        // the position of the selection
        int sel = pos + textField.getSelectedText().length();

        // make the position and selection in order
        int start = Math.min(pos, sel);
        int end = Math.max(pos, sel);

        ILocation loc = getLocation();

        for (StringRenderable text : getWrappedLines()) {

            // cursor drawing
            if (pos >= 0 && pos <= text.getString().length()) {
                // cursor is on this line
                int c = fr.getWidth(text.getString().substring(0, pos));
                boolean cursorBlink = this.cursorCounter / 6 % 3 != 0;
                if (cursorBlink) {
                    if (textField.getCursor() < this.textField.getValue().length()) {
                        drawVerticalLine(matrixStack, loc.getXPos() + c + 3,
                                loc.getYPos() + line - 2,
                                loc.getYPos() + line + fr.fontHeight + 1, 0xffd0d0d0);
                    } else {
                        fr.draw(matrixStack, "_", loc.getXPos() + c + 2, loc.getYPos() + line + 1, getPrimaryColorProperty().getHex());
                    }

                }
            }

            // selection highlighting

            // the start of the highlight.
            int x = -1;
            // the end of the highlight.
            int w = -1;

            // test the start
            if (start >= 0 && start <= text.getString().length()) {
                x = fr.getWidth(text.getString().substring(0, start));
            }

            // test the end
            if (end >= 0 && end <= text.getString().length()) {
                w = fr.getWidth(text.getString().substring(Math.max(start, 0), end)) + 2;
            }

            final int LINE_Y = line + fr.fontHeight + 2;
//System.out.println("ds");
            if (w != 0) {
                //System.out.println("test");
                if (x >= 0 && w > 0) {
                    // start and end on same line
                    drawSelectionBox(x + 2, line, x + w, LINE_Y);
                } else {
                    if (x >= 0) {
                        // started on this line
                        drawSelectionBox(x + 2, line, x + fr.getWidth(text.getString().substring(start)) + 1, LINE_Y);
                    }
                    if (w >= 0) {
                        // ends on this line
                        drawSelectionBox(2, line, w, LINE_Y);
                    }
                    if (start < 0 && end > text.getString().length()) {
                        // full line
                        drawSelectionBox(1, line, fr.getWidth(text), LINE_Y);
                    }
                }
            }

            // keep track of the lines
            totalPos += text.getString().length();
            boolean space = getText().length() > totalPos && getText().charAt(totalPos) == ' ';

            // prepare all the markers for the next line.
            pos -= text.getString().length();
            start -= text.getString().length();
            end -= text.getString().length();

            if (space) {
                // compensate for spaces
                pos--;
                start--;
                end--;
                totalPos++;
            }
            line = LINE_Y;
        }

    }

    private void drawText(MatrixStack matrixStack) {
        FancyFontRenderer ffr = new FancyFontRenderer(fr);
        ILocation loc = getLocation();
        int xPos = loc.getXPos() + 3;
        int yPos = loc.getYPos() + 1;
        List<MutableText> lines = getFormattedLines();
        for (MutableText line : lines) {
            Color color = Color.WHITE;
            xPos = loc.getXPos() + 3;
            ffr.drawChat(matrixStack, line, xPos, yPos, color.getHex(), false);
            yPos += fr.fontHeight + 2;
            xPos += fr.getWidth(line.getString());
        }
        yPos -= fr.fontHeight + 2;

        boolean flag2 = textField.getTextField().getCursor() < getText().length() || getText().length() >= ((MixinTextFieldWidget)textField.getTextField()).invokeGetMaxLength();
        //System.out.println(flag2);

        int x = loc.getXPos() + 3;
        if (!flag2 && suggestion != null) {
            this.fr.drawWithShadow(matrixStack, this.suggestion, xPos, yPos, -8355712);
        }

    }

    /**
     * Draws the blue selection box. Forwards to {@link MixinTextFieldWidget#invokeDrawSelectionHighlight(int, int, int, int)}
     */
    private void drawSelectionBox(int x1, int y1, int x2, int y2) {
        ILocation loc = getLocation();
        x1 += loc.getXPos();
        x2 += loc.getXPos();
        y1 += loc.getYPos();
        y2 += loc.getYPos();

        //System.out.println(x1 + " " + x2 + " " + y1 + " " + y2);

        ((MixinTextFieldWidget)this.textField.getTextField()).invokeDrawSelectionHighlight(x1, y1, x2, y2);
//        if (x1 < x2) {
//            int i = x1;
//            x1 = x2;
//            x2 = i;
//        }
//
//        if (y1 < y2) {
//            int j = y1;
//            y1 = y2;
//            y2 = j;
//        }
//
//        x2 = Math.min(x2, this.getLocation().getXWidth());
//        x1 = Math.min(x1, this.getLocation().getXWidth());
//
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder bufferbuilder = tessellator.getBuffer();
//        GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
//        GlStateManager.disableTexture();
//        GlStateManager.enableColorLogicOp();
//        GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
//        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//        bufferbuilder.pos(x1, y2, 0.0D).endVertex();
//        bufferbuilder.pos(x2, y2, 0.0D).endVertex();
//        bufferbuilder.pos(x2, y1, 0.0D).endVertex();
//        bufferbuilder.pos(x1, y1, 0.0D).endVertex();
//        tessellator.draw();
//        GlStateManager.disableColorLogicOp();
//        GlStateManager.enableTexture();
    }

    @Override
    public void tick() {
        this.cursorCounter++;
    }

    public List<StringRenderable> getWrappedLines() {
        return fr.wrapLines(StringRenderable.plain(textField.getValue()), getLocation().getWidth());
    }

    private List<MutableText> getFormattedLines() {
        List<MutableText> lines = new ArrayList<>();
        for (StringRenderable line : getWrappedLines()) {
            lines.add(new LiteralText(line.getString()));
        }
        return lines;
    }

    public void setTextFormatter(BiFunction<String, Integer, String> textFormatter) {
        this.textFormatter = textFormatter;
    }

    @Override
    @Nonnull
    public Dim getMinimumSize() {
        return new Dim(100, (fr.fontHeight + 2) * getWrappedLines().size());
    }

    public GuiText getTextField() {
        return textField;
    }

    public String getText() {
        return textField.getValue();
    }

    public void setText(String text) {
        textField.setValue(text);
    }

    @Override
    public boolean charTyped(char key, int mods) {
        return IGuiEventListenerDelegate.super.charTyped(key, mods);
    }

    @Override
    public boolean mouseClicked(double x, double y, int mouseButton) {
        if (mouseButton == 0) {
            ILocation bounds = this.getLocation();

            int width = bounds.getWidth() - 1;
            int row = (int) y / (fr.fontHeight + 2);

            List<StringRenderable> lines = getWrappedLines();
            if (row < 0 || row >= lines.size() || x < 0 || x > width) {
                return false;
            }
            int index = 0;
            for (int i = 0; i < row; i++) {
                index += lines.get(i).getString().length();
                // check for spaces because trailing spaces are trimmed
                if (getText().charAt(index) == ' ') {
                    index++;
                }
            }
            index += fr.trimToWidth(lines.get(row), (int) x - 3).getString().length();
            textField.getTextField().setCursor(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && mc.inGameHud.getChatHud().isChatFocused();
    }
}

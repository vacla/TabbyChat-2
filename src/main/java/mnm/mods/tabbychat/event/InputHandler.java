package mnm.mods.tabbychat.event;

import fi.dy.masa.malilib.hotkeys.*;

public class InputHandler implements IKeyboardInputHandler, IMouseInputHandler
{

    private static final InputHandler INSTANCE = new InputHandler();

    private InputHandler()
    {
    }

    public static InputHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        if(eventKeyState)
        {

        }
    }
}
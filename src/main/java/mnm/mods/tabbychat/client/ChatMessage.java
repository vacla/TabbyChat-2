package mnm.mods.tabbychat.client;

import java.time.LocalDateTime;

import mnm.mods.tabbychat.api.Message;
import net.minecraft.text.Text;

public class ChatMessage implements Message {

    private Text message;
    private int id;
    private transient int counter;
    private LocalDateTime instant;

    ChatMessage() {}

    public ChatMessage(int updatedCounter, Text chat, int id, boolean isNew) {
        // super(updatedCounter, chat, id);
        this.message = chat;
        this.id = id;
        this.counter = updatedCounter;
        if (isNew) {
            this.instant = LocalDateTime.now();
        }
    }

    @Override
    public Text getMessage() {
        return this.message;
    }

    public int getCounter() {
        return this.counter;
    }

    public int getID() {
        return this.id;
    }

    @Override
    public LocalDateTime getDateTime() {
        return this.instant;
    }

}

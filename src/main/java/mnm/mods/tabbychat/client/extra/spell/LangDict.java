package mnm.mods.tabbychat.client.extra.spell;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import mnm.mods.tabbychat.TabbyChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public interface LangDict {

    LangDict ENGLISH = fromLanguage("en_us");

    InputStream openStream() throws IOException;

    static LangDict fromLanguage(String lang) {
        String path = String.format("dicts/%s.dic", lang);
        if (Files.isRegularFile(TabbyChat.dataFolder.toPath().resolve(path))) {
            return () -> Files.newInputStream(TabbyChat.dataFolder.toPath().resolve(path));
        } else {
            Identifier res = new Identifier(TabbyChat.MODID, path);
            ResourceManager resmgr = MinecraftClient.getInstance().getResourceManager();
            return () -> resmgr.getResource(res).getInputStream();
        }
    }
}

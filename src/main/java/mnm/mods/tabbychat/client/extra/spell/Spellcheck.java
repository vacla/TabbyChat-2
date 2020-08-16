package mnm.mods.tabbychat.client.extra.spell;

import com.google.common.collect.Lists;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import mnm.mods.tabbychat.TCMarkers;
import mnm.mods.tabbychat.TabbyChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Spellcheck implements SynchronousResourceReloadListener
{

    private final Path userFile;

    private SpellChecker spellCheck;
    private SpellDictionary userDict;
    private List<SpellCheckEvent> errors = Lists.newArrayList();

    public Spellcheck(Path configDir) {
        userFile = configDir.resolve("userdict.txt");
        this.loadCurrentLanguage();
    }

    public synchronized void loadDictionary(LangDict lang) throws IOException {
        try (Reader read = new InputStreamReader(openLangStream(lang))) {
            SpellDictionary dictionary = new SpellDictionaryHashMap(read);
            spellCheck = new SpellChecker(dictionary);
            spellCheck.setUserDictionary(userDict);
            spellCheck.addSpellCheckListener(errors::add);
        }
    }

    private InputStream openLangStream(LangDict lang) throws IOException {
        try {
            TabbyChat.logger.warn(lang);
            return lang.openStream();
        } catch (FileNotFoundException e) {
            if (lang == LangDict.ENGLISH) {
                // Prevent StackOverflowException
                throw e;
            }
            TabbyChat.logger.warn(TCMarkers.SPELLCHECK, "Error loading dictionary. Falling back to en_us.", e);
            return openLangStream(LangDict.ENGLISH);
        }
    }

    public synchronized void loadUserDictionary() throws IOException {
        if (Files.notExists(userFile)) {
            Files.createDirectories(userFile.getParent());
            Files.createFile(userFile);
            try (BufferedWriter w = Files.newBufferedWriter(userFile)) {
                w.write("# User dictionary, one entry per line.");
            }
        }
        try (Reader r = Files.newBufferedReader(userFile)) {
            this.userDict = new UserDictionary(r);

            // set it if it has been created yet.
            if (this.spellCheck != null) {
                this.spellCheck.setUserDictionary(this.userDict);
            }
        }
    }

    public synchronized void addToDictionary(String word) {
        // add to user dictionary
        this.userDict.addWord(word);
    }

    public Path getUserDictionaryFile() {
        return userFile;
    }

    public Iterable<SpellCheckEvent> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public void checkSpelling(String string) {
        if (spellCheck != null) {
            this.errors.clear();
            this.spellCheck.checkSpelling(new StringWordTokenizer(string));
        }
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        loadCurrentLanguage();
    }

    private void loadCurrentLanguage() {
        LanguageDefinition lang = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        try {
            loadUserDictionary();
            loadDictionary(LangDict.fromLanguage(lang.getCode()));
        } catch (IOException e) {
            TabbyChat.logger.warn(TCMarkers.SPELLCHECK, "Error while loading dictionary {}.", lang.getCode(), e);
        }
    }

}

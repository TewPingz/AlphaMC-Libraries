package rip.alpha.libraries.tablist;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import rip.alpha.libraries.skin.MojangSkin;
import rip.alpha.libraries.util.message.MessageTranslator;

public class TabListLayout {
    public static final TabListEntry EMPTY_ENTRY = new TabListEntry("                                ");

    @Getter
    private final Int2ObjectMap<TabListEntry> entryMap;

    public TabListLayout(boolean v1_8) {
        int size = v1_8 ? 80 : 60;
        this.entryMap = new Int2ObjectOpenHashMap<>(size);
        for (int i = 0; i < size; i++) {
            this.entryMap.put(i, EMPTY_ENTRY.clone());
        }
    }

    public void put(int column, int row, String text, MojangSkin skin) {
        this.put(TabListUtils.convertToSlot(row, column), text, skin);
    }

    public void put(int column, int row, String text) {
        this.put(TabListUtils.convertToSlot(row, column), text, null);
    }

    public void put(int index, String text, MojangSkin skin) {
        if (index >= this.entryMap.size()) {
            return;
        }

        TabListEntry entry = new TabListEntry(MessageTranslator.translate(text));
        if (skin != null) {
            entry.setSkin(skin);
        }
        this.entryMap.put(index, entry);
    }
}

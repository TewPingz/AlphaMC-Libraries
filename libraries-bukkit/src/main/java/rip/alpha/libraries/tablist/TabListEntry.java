package rip.alpha.libraries.tablist;

import lombok.Getter;
import lombok.Setter;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.skin.MojangSkin;

@Getter
@Setter
public class TabListEntry {
    private static final MojangSkin GRAY_SKIN = LibrariesPlugin.getInstance().getMojangSkinHandler().getLetterSkin("gray", ' ');

    private String text;
    private int ping = 0;
    private MojangSkin skin = GRAY_SKIN;

    public TabListEntry(String text) {
        this.text = text;
    }

    public TabListEntry(int row, int column, String text, MojangSkin skin) {
        this.text = text;
        this.setSkin(skin);
    }

    public void reset() {
        this.skin = GRAY_SKIN;
        this.ping = 0;
        this.text = "";
    }

    public void update(TabListEntry entry) {
        this.text = entry.getText();
        this.ping = entry.getPing();
        this.skin = entry.getSkin();
    }

    public boolean isSameSkin(TabListEntry entry) {
        return this.skin.value().equals(entry.getSkin().value());
    }

    public boolean isSamePing(TabListEntry entry) {
        return this.ping == entry.getPing();
    }

    public boolean isSameText(TabListEntry entry) {
        return this.text.equals(entry.getText());
    }

    public boolean compare(TabListEntry entry) {
        return isSamePing(entry) && isSameSkin(entry) && isSameText(entry);
    }

    public void setSkin(MojangSkin skin) {
        if (skin == null) {
            this.skin = GRAY_SKIN;
            return;
        }
        this.skin = skin;
    }

    @Override
    public TabListEntry clone() {
        TabListEntry entry = new TabListEntry(text);
        this.setSkin(this.skin);
        entry.setPing(this.ping);
        return entry;
    }
}

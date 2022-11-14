package rip.alpha.libraries;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import rip.alpha.libraries.board.BoardHandler;
import rip.alpha.libraries.chatinput.ChatInputListener;
import rip.alpha.libraries.command.CommandFramework;
import rip.alpha.libraries.fake.FakeEntityHandler;
import rip.alpha.libraries.gui.MenuListener;
import rip.alpha.libraries.hologram.HologramHandler;
import rip.alpha.libraries.json.GsonProvider;
import rip.alpha.libraries.listener.PlayerListener;
import rip.alpha.libraries.nametag.NametagHandler;
import rip.alpha.libraries.skin.MojangSkinHandler;
import rip.alpha.libraries.spawner.SpawnerHandler;
import rip.alpha.libraries.tablist.TabListHandler;
import rip.alpha.libraries.timer.Timer;
import rip.alpha.libraries.util.BlockUtil;
import rip.alpha.libraries.util.EmptyIncomingChannelHandler;
import rip.alpha.libraries.util.task.TaskUtil;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LibrariesPlugin {

    @Getter
    private static final LibrariesPlugin instance = new LibrariesPlugin();

    private JavaPlugin bukkitPlugin;
    private CommandFramework commandFramework;
    private MojangSkinHandler mojangSkinHandler;
    private FakeEntityHandler fakeEntityHandler;
    private SpawnerHandler spawnerHandler;
    private BoardHandler boardHandler;
    private TabListHandler tabListHandler;
    private NametagHandler nametagHandler;
    private HologramHandler hologramHandler;

    public void enable(JavaPlugin plugin) {
        Libraries.getInstance().enable();
        GsonProvider.registerAbstractClass(Timer.class);

        this.bukkitPlugin = plugin;
        this.commandFramework = new CommandFramework(plugin);
        this.mojangSkinHandler = new MojangSkinHandler(this);
        this.fakeEntityHandler = new FakeEntityHandler(this.getBukkitPlugin());
        this.spawnerHandler = new SpawnerHandler(this);
        this.hologramHandler = new HologramHandler(this);

        Server server = plugin.getServer();
        Messenger messenger = server.getMessenger();
        messenger.registerOutgoingPluginChannel(this.getBukkitPlugin(), "BungeeCord");
        messenger.registerIncomingPluginChannel(this.getBukkitPlugin(), "BungeeCord", new EmptyIncomingChannelHandler());
        messenger.registerOutgoingPluginChannel(this.getBukkitPlugin(), "BungeeUtils");
        messenger.registerIncomingPluginChannel(this.getBukkitPlugin(), "BungeeUtils", new EmptyIncomingChannelHandler());
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), this.getBukkitPlugin());
        pluginManager.registerEvents(new MenuListener(), this.getBukkitPlugin());
        pluginManager.registerEvents(new ChatInputListener(), this.getBukkitPlugin());

        BlockUtil.init();
    }

    public void disable() {
        TaskUtil.shutdownThreadPoolExecutor();
        this.mojangSkinHandler.saveUUIDSkinCache();
        Libraries.getInstance().disable();
    }

    public void registerTabListHandler(TabListHandler tabListHandler) {
        this.tabListHandler = tabListHandler;
    }

    public void registerNametagHandler(NametagHandler nametagHandler) {
        this.nametagHandler = nametagHandler;
    }

    public void registerBoardHandler(BoardHandler boardHandler) {
        this.boardHandler = boardHandler;
    }

    public boolean isTabListHandlerEnabled() {
        return this.tabListHandler != null;
    }

    public static CommandFramework getCommandFramework() {
        return instance.commandFramework;
    }
}

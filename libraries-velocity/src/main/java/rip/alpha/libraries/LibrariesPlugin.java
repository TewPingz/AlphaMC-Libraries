package rip.alpha.libraries;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LibrariesPlugin {

    @Getter
    private static final LibrariesPlugin instance = new LibrariesPlugin();

    private Object plugin;
    private ProxyServer proxyServer;

    public void enable(Object plugin, ProxyServer proxyServer) {
        Libraries.getInstance().enable();

        this.plugin = plugin;
        this.proxyServer = proxyServer;
    }

    public void disable() {
        Libraries.getInstance().disable();
    }
}

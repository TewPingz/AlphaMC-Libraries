package rip.alpha.libraries.command;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rip.alpha.libraries.LibrariesPlugin;
import rip.alpha.libraries.command.annotation.Command;
import rip.alpha.libraries.command.annotation.CommandDescription;
import rip.alpha.libraries.command.context.ContextResolver;
import rip.alpha.libraries.command.context.impl.bukkit.*;
import rip.alpha.libraries.command.context.impl.java.*;
import rip.alpha.libraries.command.type.BaseCommand;
import rip.alpha.libraries.command.type.RootCommand;
import rip.alpha.libraries.util.PackageUtil;
import rip.foxtrot.spigot.fSpigot;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;

public class CommandFramework {
    private final SimpleCommandMap commandMap;
    private final Map<Class<?>, ContextResolver<?>> registeredContextResolvers;
    private final Map<String, BaseCommand> registeredCommands;

    public CommandFramework(JavaPlugin javaPlugin) {
        this.commandMap = MinecraftServer.getServer().server.getCommandMap();
        this.registeredContextResolvers = new HashMap<>();
        this.registeredCommands = new HashMap<>();

        javaPlugin.getServer().getPluginManager().registerEvents(new CommandListener(), javaPlugin);

        // TabComplete handler
        fSpigot.INSTANCE.addTabCompleteHandler(new CommandTabCompleteHandler());

        //Java parameters
        this.registerContextResolver(new BooleanContext(), boolean.class, Boolean.class);
        this.registerContextResolver(new DoubleContext(), double.class, Double.class);
        this.registerContextResolver(new IntegerContext(), int.class, Integer.class);
        this.registerContextResolver(new FloatContext(), float.class, Float.class);
        this.registerContextResolver(new LongContext(), long.class, Long.class);
        this.registerContextResolver(new UUIDContext(), UUID.class);
        this.registerContextResolver(new StringContext(), String.class);
        this.registerContextResolver(new EnumContext(), Enum.class);
        this.registerContextResolver(new DurationContext(), Duration.class);

        //Bukkit parameters
        this.registerContextResolver(new PlayerContext(), Player.class);
        this.registerContextResolver(new OfflinePlayerContext(), OfflinePlayer.class);
        this.registerContextResolver(new WorldContext(), World.class);
        this.registerContextResolver(new GameModeContext(), GameMode.class);
        this.registerContextResolver(new EntityTypeContext(), EntityType.class);
        this.registerContextResolver(new EnchantmentContext(), Enchantment.class);
    }

    public <T> ContextResolver<T> getContextResolver(Class<?> clazz) {
        ContextResolver<T> resolver = (ContextResolver<T>) this.registeredContextResolvers.get(clazz);

        Class<?> superClazz = clazz.getSuperclass();
        if (resolver == null && superClazz != null) {
            return this.getContextResolver(superClazz);
        }

        return resolver;
    }

    @SafeVarargs
    public final <T> void registerContextResolver(ContextResolver<T> contextResolver, Class<T>... classes) {
        Set<IllegalStateException> exceptions = new HashSet<>();

        for (Class<?> clazz : classes) {
            if (this.registeredContextResolvers.containsKey(clazz)) {
                exceptions.add(new IllegalStateException("There is already a registered context resolver for " + clazz.getSimpleName()));
            }
            this.registeredContextResolvers.put(clazz, contextResolver);
        }

        exceptions.forEach(Throwable::printStackTrace);
    }

    public void registerPackage(String packageName) {
        this.registerClasses(PackageUtil.getClasses(LibrariesPlugin.getInstance(), packageName));
    }

    public void registerClasses(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            this.registerClass(clazz);
        }
    }

    public void registerClass(Class<?> clazz) {
        Set<IllegalArgumentException> exceptions = new HashSet<>();

        methodLoop:
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);

            if (method.getParameterCount() <= 0) {
                continue;
            }

            if (method.getReturnType() != void.class) {
                continue;
            }

            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            Command command = method.getAnnotation(Command.class);

            if (command == null || command.names().length <= 0) {
                continue;
            }

            Parameter[] parameters = method.getParameters();

            if (parameters.length > 1) {
                for (int i = 2; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (this.getContextResolver(parameter.getType()) == null) {
                        exceptions.add(new IllegalArgumentException(command.names()[0] + " with " + parameter.getType().getSimpleName() + " but no context resolver registered"));
                        continue methodLoop;
                    }
                }
            }

            this.registerCommand(method, command);
        }

        exceptions.forEach(Throwable::printStackTrace);
    }

    protected void registerCommand(Method method, Command command) {
        for (String commandLabel : command.names()) {
            String[] args = commandLabel.split(" ");
            String rootName = args[0];
            BaseCommand baseCommand = this.getOrCreateBase(rootName);

            try {
                if (args.length == 1) {
                    this.registerCommand(baseCommand, method, command);
                } else {
                    String label = args[1];
                    RootCommand subCommand = baseCommand.getOrCreateSubCommand(label);

                    if (!(args.length == 2)) {
                        for (int i = 2; i < args.length; i++) {
                            subCommand = subCommand.getOrCreateSubCommand(args[i]);
                        }
                    }

                    this.registerCommand(subCommand, method, command);
                }
            } catch (Exception e) {
                baseCommand.getBukkitCommand().unregister(this.commandMap);
                e.printStackTrace();
            }
        }
    }

    private void registerCommand(RootCommand rootCommand, Method method, Command command) throws IllegalAccessException {
        rootCommand.setMethod(method);
        rootCommand.setAsync(command.async());
        rootCommand.setPermission(command.permission());
        rootCommand.setPlayerOnly(method.getParameters()[0].getType() == Player.class);

        CommandDescription description = method.getAnnotation(CommandDescription.class);
        if (description != null) {
            rootCommand.setDescription(description.value());
        }
    }

    private BaseCommand getOrCreateBase(String rootName) {
        BaseCommand baseCommand = this.getBaseCommand(rootName);

        if (baseCommand != null) {
            return baseCommand;
        }

        baseCommand = new BaseCommand(rootName);
        this.registeredCommands.put(rootName.toLowerCase(), baseCommand);

        new HashSet<>(this.commandMap.getCommands()).forEach(command -> {
            if (command.getLabel().equalsIgnoreCase(rootName)) {
                command.unregister(this.commandMap);
                String fallbackPrefix = "fallback";

                if (command instanceof PluginCommand) {
                    fallbackPrefix = ((PluginCommand) command).getPlugin().getName().toLowerCase();
                }

                this.commandMap.register(command.getLabel(), fallbackPrefix, command);
            }
        });

        this.commandMap.register(rootName, rootName, baseCommand.getBukkitCommand());

        return baseCommand;
    }

    protected BaseCommand getBaseCommand(String rootName) {
        return this.registeredCommands.get(rootName.toLowerCase());
    }
}

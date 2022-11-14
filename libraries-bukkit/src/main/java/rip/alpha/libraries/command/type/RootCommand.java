package rip.alpha.libraries.command.type;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.libraries.command.CommandPreconditions;
import rip.alpha.libraries.command.annotation.CommandUsage;
import rip.alpha.libraries.command.annotation.Default;
import rip.alpha.libraries.command.annotation.Optional;
import rip.alpha.libraries.command.annotation.Wildcard;
import rip.alpha.libraries.command.context.ContextEntry;
import rip.alpha.libraries.util.message.MessageColor;
import rip.alpha.libraries.util.message.MessageTranslator;
import rip.alpha.libraries.util.task.TaskUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class RootCommand {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final String line = MessageTranslator.translate("&7&m------------------------------");

    protected final Map<String, RootCommand> subCommandMap;
    protected final Int2ObjectMap<ContextEntry<?>> contextMap;

    @Getter(AccessLevel.PROTECTED)
    protected final String label;

    private MethodHandle methodInvoker;

    @Getter
    @Setter
    private String permission, description, usageFormat, annotationUsageFormat;

    @Getter
    @Setter
    private boolean playerOnly, async;

    public RootCommand(String label) {
        this.label = label;
        this.subCommandMap = new HashMap<>();
        this.contextMap = new Int2ObjectOpenHashMap<>();
    }

    protected void executeCommand(CommandSender commandSender, String label, String[] args) {
        if (this.subCommandMap.size() > 0) {
            if (args.length > 0) {
                RootCommand subCommand = this.getSubCommand(args[0]);
                if (subCommand != null) {
                    subCommand.executeCommand(commandSender, label + " " + subCommand.getLabel(), Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        if (this.methodInvoker == null) {
            if (this.subCommandMap.size() <= 0) {
                return;
            }

            List<String> messages = new ArrayList<>();
            for (RootCommand command : this.getSubCommands()) {
                if (!command.hasPermission(commandSender)) {
                    continue;
                }

                String format = MessageColor.WHITE + " " + "/" + label + " " + command.getLabel();

                if (command.getAnnotationUsageFormat() != null) {
                    format += " " + command.getAnnotationUsageFormat();
                }

                String description = command.getDescription();

                if (description != null && !description.isEmpty()) {
                    format = format + ChatColor.GRAY + " (" + description + ")";
                }

                messages.add(format);
            }

            if (!messages.isEmpty()) {
                commandSender.sendMessage(line);
                commandSender.sendMessage(MessageTranslator.translate("&6&l" + StringUtils.capitalize(label.toLowerCase()) + " Commands"));
                messages.forEach(commandSender::sendMessage);
                commandSender.sendMessage(line);
            }

            return;
        }

        if (this.playerOnly && !(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You need to be a player to execute this command.");
            return;
        }

        if (!this.hasPermission(commandSender)) {
            commandSender.sendMessage(ChatColor.RED + "You dont have permission to execute this command.");
            return;
        }

        int requiredParameters = 0;

        for (ContextEntry<?> entry : this.contextMap.values()) {
            if (entry.getDefaultValue() == null && !entry.isOptional()) {
                requiredParameters++;
            }
        }

        if (args.length < requiredParameters) {
            commandSender.sendMessage(String.format(this.usageFormat, label));
            return;
        }

        this.preformCommand(commandSender, args, this.async);
    }

    protected void preformCommand(CommandSender commandSender, String[] arguments, boolean async) {
        int parameterSize = this.contextMap.size();

        CompletableFuture.supplyAsync(() -> {
            Object[] params = new Object[parameterSize + 1];
            params[0] = commandSender;

            for (int i = 0; i < this.contextMap.size(); i++) {
                ContextEntry<?> entry = this.contextMap.get(i);
                Object value = null;

                if (entry == null) {
                    throw new NullPointerException("There was a null parameter entry");
                }

                String encodedValue = null;

                if (entry.isWildcard()) {
                    encodedValue = StringUtils.join(arguments, " ", i, arguments.length);
                } else if (!(i >= arguments.length)) {
                    encodedValue = arguments[i];
                }

                if (encodedValue != null && encodedValue.isBlank()) {
                    encodedValue = null;
                }

                if (encodedValue == null && entry.getDefaultValue() != null) {
                    encodedValue = entry.getDefaultValue();
                }

                if (encodedValue != null) {
                    value = entry.transform(commandSender, encodedValue);
                }

                if (!entry.isOptional() && value == null) {
                    return null;
                }

                params[i + 1] = value;
            }

            return params;
        }).thenAccept(params -> {
            if (params == null) {
                return;
            }

            Runnable commandExecutor = () -> {
                try {
                    this.methodInvoker.invokeWithArguments(params);
                } catch (Throwable e) {
                    commandSender.sendMessage(MessageColor.RED + "An error occurred while performing your command");
                    e.printStackTrace();
                }
            };

            if (async) {
                commandExecutor.run();
            } else {
                TaskUtil.runSync(commandExecutor);
            }
        });
    }

    public void setMethod(Method method) throws IllegalAccessException {
        if (method != null) {
            this.methodInvoker = lookup.unreflect(method);
        } else {
            this.methodInvoker = null;
        }

        this.contextMap.clear();
        this.usageFormat = MessageColor.RED + "Usage: /%s";
        this.annotationUsageFormat = null;

        if (method == null) {
            return;
        }

        CommandUsage commandUsage = method.getAnnotation(CommandUsage.class);

        Parameter[] parameters = Arrays.copyOfRange(method.getParameters(), 1, method.getParameters().length);
        int length = parameters.length;

        StringBuilder usageBuilder = new StringBuilder(this.usageFormat);

        if (commandUsage != null) {
            usageBuilder.append(" ").append(commandUsage.value());
            this.usageFormat = usageBuilder.toString();
            this.annotationUsageFormat = commandUsage.value();
        }

        for (int i = 0; i < length; i++) {
            Parameter parameter = parameters[i];

            Default defaultAnnotation = parameter.getAnnotation(Default.class);
            Optional optionalAnnotation = parameter.getAnnotation(Optional.class);
            Wildcard wildcardAnnotation = parameter.getAnnotation(Wildcard.class);

            String parameterName = parameter.getName(), defaultValue = null;
            boolean optional = false, wildcard = false;

            if (defaultAnnotation != null) {
                CommandPreconditions.checkNoRequiredArguments(i, parameters, "Added a default value parameter that is followed by a required parameter.");
                defaultValue = defaultAnnotation.value();
            } else if (optionalAnnotation != null) {
                CommandPreconditions.checkNoRequiredArguments(i, parameters, "Added an optional parameter that is followed by a required parameter.");
                optional = true;
            }

            if (wildcardAnnotation != null) {
                CommandPreconditions.checkLastArgument(i, parameters, "Added a wildcard parameter that is not last.");
                wildcard = true;
            }

            ContextEntry<?> entry = new ContextEntry<>(parameterName, defaultValue, optional, wildcard, parameter.getType());
            this.contextMap.put(i, entry);

            if (commandUsage == null) {
                usageBuilder.append(" ")
                        .append(entry.isWildcard() ? "[" : "<")
                        .append(entry.getName())
                        .append((entry.isWildcard() ? "...]" : ">"));

                this.usageFormat = usageBuilder.toString();
            }
        }
    }

    public boolean hasPermission(CommandSender commandSender) {
        if (commandSender.isOp()) {
            return true;
        }

        if (commandSender.hasPermission("*")) {
            return true;
        }

        if (this.methodInvoker == null) {
            if (this.subCommandMap.size() > 0) {
                for (RootCommand command : this.getSubCommands()) {
                    if (command.hasPermission(commandSender)) {
                        if (command.isPlayerOnly() && (!(commandSender instanceof Player))) {
                            continue;
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        if (this.permission != null && this.permission.length() > 0) {
            if (this.permission.equalsIgnoreCase("op")) {
                return false;
            }
            return commandSender.hasPermission(this.permission);
        }

        return true;
    }

    protected List<String> tabComplete(CommandSender commandSender, String label, String[] arguments) {
        if (arguments.length == 1 && this.subCommandMap.size() > 0) {
            List<String> currentList = new ArrayList<>();

            for (RootCommand command : this.getSubCommands()) {
                if (command.hasPermission(commandSender)) {
                    currentList.add(command.getLabel());
                }
            }

            return currentList;
        } else if (arguments.length > 1 && this.subCommandMap.size() > 0) {
            RootCommand subCommand = this.getSubCommand(arguments[0]);

            if (subCommand == null) {
                return null;
            }

            return subCommand.tabComplete(commandSender, label, Arrays.copyOfRange(arguments, 1, arguments.length));
        } else if (arguments.length >= 1 && this.contextMap.size() > 0) {
            ContextEntry<?> entry = this.contextMap.get(arguments.length - 1);
            if (entry != null) {
                return entry.tabComplete(commandSender);
            }
        }

        return null;
    }

    protected RootCommand getSubCommand(String name) {
        return this.subCommandMap.get(name.toLowerCase());
    }

    protected Collection<RootCommand> getSubCommands() {
        return Collections.unmodifiableCollection(this.subCommandMap.values());
    }

    public void registerSubCommand(RootCommand subCommand) {
        this.subCommandMap.put(subCommand.getLabel().toLowerCase(), subCommand);
    }

    public RootCommand getOrCreateSubCommand(String label) {
        RootCommand command = this.getSubCommand(label);

        if (command == null) {
            command = new SubCommand(label);
            this.registerSubCommand(command);
        }

        return command;
    }
}

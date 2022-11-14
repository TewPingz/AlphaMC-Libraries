package rip.alpha.libraries.command;

import rip.alpha.libraries.command.annotation.Default;
import rip.alpha.libraries.command.annotation.Optional;

import java.lang.reflect.Parameter;

public final class CommandPreconditions {
    public static void checkNoRequiredArguments(int index, Parameter[] parameters, String message) {
        for (int i = index + 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.isAnnotationPresent(Default.class)) {
                continue;
            }

            if (parameter.isAnnotationPresent(Optional.class)) {
                continue;
            }

            throw new IllegalArgumentException(message);
        }
    }

    public static void checkLastArgument(int index, Parameter[] parameters, String message) {
        if (index != (parameters.length - 1)) {
            throw new IllegalArgumentException(message);
        }
    }
}

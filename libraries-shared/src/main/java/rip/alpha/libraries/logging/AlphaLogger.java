package rip.alpha.libraries.logging;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AlphaLogger {

    @Getter(AccessLevel.PROTECTED)
    private final String name;

    public void log(Object message, LogLevel visibilityLevel) {
        AlphaLoggerFactory.log(this.name, message.toString(), visibilityLevel);
    }

    public void warn(Object message, LogLevel visibilityLevel) {
        AlphaLoggerFactory.warn(this.name, message.toString(), visibilityLevel);
    }

    public void severe(Object message, LogLevel visibilityLevel) {
        AlphaLoggerFactory.severe(this.name, message.toString(), visibilityLevel);
    }

}
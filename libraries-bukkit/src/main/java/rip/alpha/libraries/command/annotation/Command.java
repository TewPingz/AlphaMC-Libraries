package rip.alpha.libraries.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * the command and aliases
     *
     * @return the command and aliases
     */
    String[] names();

    /**
     * the permission of the command
     *
     * @return the permission needed to execute the command
     */
    String permission() default "";

    /**
     * if the command should be executed asynchronously
     *
     * @return if the command should be executed asynchronously
     */
    boolean async() default false;

}

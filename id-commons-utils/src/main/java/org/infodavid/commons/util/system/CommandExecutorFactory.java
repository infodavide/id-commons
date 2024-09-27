package org.infodavid.commons.util.system;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * The Class CommandExecutorFactory.
 */
public class CommandExecutorFactory {

    /** The instance. */
    private static CommandExecutor instance;

    /**
     * Returns the instance.
     * @return the instance
     */
    public static synchronized CommandExecutor getInstance() {
        if (instance != null) {
            return instance;
        }

        final ServiceLoader<CommandExecutor> loader = ServiceLoader.load(CommandExecutor.class);
        final Iterator<CommandExecutor> ite = loader.iterator();

        if (ite.hasNext()) {
            instance = ite.next();
        } else {
            instance = new DefaultCommandExecutor();
        }

        return instance;
    }

    /**
     * Not allowed.
     */
    private CommandExecutorFactory() {
    }
}

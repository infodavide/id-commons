package org.infodavid.commons.util.system;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * The Class CommandExecutorFactory.
 */
public class CommandExecutorFactory {

    /**
     * The Class SingletonHelper.
     */
    private static class SingletonHelper {

        /** The Constant SINGLETON. */
        private static final CommandExecutor SINGLETON;

        static {
            final ServiceLoader<CommandExecutor> loader = ServiceLoader.load(CommandExecutor.class);
            final Iterator<CommandExecutor> ite = loader.iterator();

            if (ite.hasNext()) {
                SINGLETON = ite.next();
            } else {
                SINGLETON = new DefaultCommandExecutor();
            }
        }
    }

    /**
     * Returns the instance.
     * @return the instance
     */
    public static CommandExecutor getInstance() {
        return SingletonHelper.SINGLETON;
    }

    /**
     * Not allowed.
     */
    private CommandExecutorFactory() {
    }
}

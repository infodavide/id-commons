package org.infodavid.commons.util;

import java.util.Comparator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class Comparators.
 */
public class Comparators {

    /**
     * The Class ClassNameComparator.
     */
    @SuppressWarnings("rawtypes")
    public static class ClassNameComparator implements Comparator<Class> {

        /*
         * (non-javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final Class o1, final Class o2) {
            if (o1 == o2) { // NOSONAR Compare references
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            return o1.getName().compareTo(o2.getName());
        }
    }

    /**
     * The Class HashCodeComparator.
     * @param <T> the generic type
     */
    public static class HashCodeComparator<T> implements Comparator<T> {

        /*
         * (non-javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final T o1, final T o2) {
            if (o1 == o2) { // NOSONAR Compare references
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    }

    public static class PropertiesComparator implements Comparator<Properties> {

        /** The properties. */
        private final String[] properties;

        /**
         * Instantiates a new properties comparator.
         * @param properties the properties
         */
        public PropertiesComparator(final String[] properties) {
            this.properties = properties;
        }

        /*
         * (non-javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final Properties o1, final Properties o2) {
            if (properties == null || properties.length == 0) {
                return 0;
            }

            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            for (final String property : properties) {
                final int result = StringUtils.compare(o1.getProperty(property), o2.getProperty(property));

                if (result != 0) {
                    return result;
                }
            }

            return 0;
        }
    }

    /**
     * The Class StringLengthComparator.
     */
    public static class StringLengthComparator implements Comparator<String> {

        /*
         * (non-javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final String o1, final String o2) {
            if (o1 == o2) { // NOSONAR Compare references
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }

            if (o1.length() != o2.length()) {
                return o1.length() - o2.length();
            }

            return o1.compareTo(o2);
        }
    }

    /**
     * Instantiates a new comparators.
     */
    private Comparators() {
    }
}

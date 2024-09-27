package org.infodavid.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.yevdo.jwildcard.JWildcard;

/**
 * The Class WildcardPredicate.
 */
public class WildcardPredicate implements Predicate<String> {

    /** The exclude patterns. */
    private List<Pattern> excludePatterns;

    /** The include patterns. */
    private List<Pattern> includePatterns;

    /**
     * Instantiates a new predicate.
     * @param includes the includes
     * @param excludes the excludes
     */
    public WildcardPredicate(final String[] includes, final String[] excludes) {
        if (includes == null || includes.length == 0) {
            includePatterns = Collections.emptyList();
        } else {
            includePatterns = new ArrayList<>();

            for (final String item : includes) {
                includePatterns.add(Pattern.compile(JWildcard.wildcardToRegex(item)));
            }
        }

        if (excludes == null || excludes.length == 0) {
            excludePatterns = Collections.emptyList();
        } else {
            excludePatterns = new ArrayList<>();

            for (final String item : excludes) {
                excludePatterns.add(Pattern.compile(JWildcard.wildcardToRegex(item)));
            }
        }
    }

    /*
     * (non-javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
     */
    @Override
    public boolean test(final String value) { // NOSONAR No complexity
        if (value == null) {
            return false;
        }

        if (!includePatterns.isEmpty()) {
            for (final Pattern pattern : includePatterns) {
                if (pattern.matcher(value).matches()) {
                    return true;
                }
            }
        }

        if (!excludePatterns.isEmpty()) {
            for (final Pattern pattern : excludePatterns) {
                if (pattern.matcher(value).matches()) {
                    return false;
                }
            }
        }

        return true;
    }
}

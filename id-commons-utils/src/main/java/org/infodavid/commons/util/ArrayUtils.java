package org.infodavid.commons.util;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * The Class ArrayUtils.
 */
@SuppressWarnings("static-method")
@JsonIgnoreType
public final class ArrayUtils {

    /** The singleton. */
    private static WeakReference<ArrayUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized ArrayUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new ArrayUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new utilities.
     */
    private ArrayUtils() {
    }

    /**
     * Ends with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean endsWith(final byte[] pattern, final byte[] input) {
        return isMatch(pattern, input, input.length - pattern.length - 1);
    }

    /**
     * Ends with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean endsWith(final double[] pattern, final double[] input) {
        return isMatch(pattern, input, input.length - pattern.length - 1);
    }

    /**
     * Ends with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean endsWith(final int[] pattern, final int[] input) {
        return isMatch(pattern, input, input.length - pattern.length - 1);
    }

    /**
     * Ends with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean endsWith(final short[] pattern, final short[] input) {
        return isMatch(pattern, input, input.length - pattern.length - 1);
    }

    /**
     * First.
     * @param <T>    the generic type
     * @param values the values
     * @return the t
     */
    public <T> T first(T[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        return values[0];
    }

    /**
     * Checks if is match.
     * @param pattern  the pattern
     * @param input    the input
     * @param position the position
     * @return true, if is match
     */
    public boolean isMatch(final byte[] pattern, final byte[] input, final int position) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[position + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is match.
     * @param pattern the pattern
     * @param input   the input
     * @param pos     the position
     * @return true, if is match
     */
    public boolean isMatch(final double[] pattern, final double[] input, final int pos) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is match.
     * @param pattern the pattern
     * @param input   the input
     * @param pos     the position
     * @return true, if is match
     */
    public boolean isMatch(final float[] pattern, final float[] input, final int pos) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is match.
     * @param pattern the pattern
     * @param input   the input
     * @param pos     the position
     * @return true, if is match
     */
    public boolean isMatch(final int[] pattern, final int[] input, final int pos) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if is match.
     * @param pattern the pattern
     * @param input   the input
     * @param pos     the position
     * @return true, if is match
     */
    public boolean isMatch(final short[] pattern, final short[] input, final int pos) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Last.
     * @param <T>    the generic type
     * @param values the values
     * @return the t
     */
    public <T> T last(T[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        return values[values.length - 1];
    }

    /**
     * Split.
     * @param pattern  the pattern
     * @param input    the input
     * @param preserve true to preserve tokens
     * @return the list
     */
    public List<byte[]> split(final byte[] pattern, final byte[] input, final boolean preserve) {
        final List<byte[]> results = new LinkedList<>();
        int start = 0;
        int i = 0;

        while (i < input.length) {
            if (isMatch(pattern, input, i)) {
                results.add(Arrays.copyOfRange(input, start, preserve ? i + pattern.length : i));
                start = i + pattern.length;
                i = start;
            }

            i++;
        }

        results.add(Arrays.copyOfRange(input, start, input.length));

        return results;
    }

    /**
     * Split.
     * @param pattern  the pattern
     * @param input    the input
     * @param preserve true to preserve tokens
     * @return the list
     */
    public List<double[]> split(final double[] pattern, final double[] input, final boolean preserve) {
        final List<double[]> results = new LinkedList<>();
        int start = 0;
        int i = 0;

        while (i < input.length) {
            if (isMatch(pattern, input, i)) {
                results.add(Arrays.copyOfRange(input, start, preserve ? i + pattern.length : i));
                start = i + pattern.length;
                i = start;
            }

            i++;
        }

        results.add(Arrays.copyOfRange(input, start, input.length));

        return results;
    }

    /**
     * Split.
     * @param pattern  the pattern
     * @param input    the input
     * @param preserve true to preserve tokens
     * @return the list
     */
    public List<int[]> split(final int[] pattern, final int[] input, final boolean preserve) {
        final List<int[]> results = new LinkedList<>();
        int start = 0;
        int i = 0;

        while (i < input.length) {
            if (isMatch(pattern, input, i)) {
                results.add(Arrays.copyOfRange(input, start, preserve ? i + pattern.length : i));
                start = i + pattern.length;
                i = start;
            }

            i++;
        }

        results.add(Arrays.copyOfRange(input, start, input.length));

        return results;
    }

    /**
     * Split.
     * @param pattern  the pattern
     * @param input    the input
     * @param preserve true to preserve tokens
     * @return the list
     */
    public List<short[]> split(final short[] pattern, final short[] input, final boolean preserve) {
        final List<short[]> results = new LinkedList<>();
        int start = 0;
        int i = 0;

        while (i < input.length) {
            if (isMatch(pattern, input, i)) {
                results.add(Arrays.copyOfRange(input, start, preserve ? i + pattern.length : i));
                start = i + pattern.length;
                i = start;
            }

            i++;
        }

        results.add(Arrays.copyOfRange(input, start, input.length));

        return results;
    }

    /**
     * Starts with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean startsWith(final byte[] pattern, final byte[] input) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Starts with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean startsWith(final double[] pattern, final double[] input) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Starts with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean startsWith(final int[] pattern, final int[] input) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Starts with.
     * @param pattern the pattern
     * @param input   the input
     * @return true, if successful
     */
    public boolean startsWith(final short[] pattern, final short[] input) {
        if (input.length < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[i]) {
                return false;
            }
        }

        return true;
    }
}

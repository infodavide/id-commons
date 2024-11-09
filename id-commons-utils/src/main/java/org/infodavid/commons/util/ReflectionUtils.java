package org.infodavid.commons.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.ClassUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ReflectionUtils.
 */
@JsonIgnoreType
@UtilityClass
@Slf4j
public final class ReflectionUtils {

    /** The primitive map. */
    private static final BidiMap<Class<?>, Class<?>> primitiveMap;

    static {
        primitiveMap = new DualHashBidiMap<>();
        primitiveMap.put(boolean.class, Boolean.class);
        primitiveMap.put(byte.class, Byte.class);
        primitiveMap.put(char.class, Character.class);
        primitiveMap.put(double.class, Double.class);
        primitiveMap.put(float.class, Float.class);
        primitiveMap.put(int.class, Integer.class);
        primitiveMap.put(long.class, Long.class);
        primitiveMap.put(short.class, Short.class);
    }

    /**
     * Adds the all methods.
     * @param methods      the methods
     * @param concreteOnly the concrete only flag
     * @param result       the result
     */
    private void addAllMethods(final Method[] methods, final boolean concreteOnly, final Set<Method> result) {
        if (methods == null || methods.length == 0) {
            return;
        }

        if (concreteOnly) {
            result.addAll(Arrays.stream(methods).filter(m -> !Modifier.isAbstract(m.getModifiers())).toList());
        } else {
            Collections.addAll(result, methods);
        }
    }

    /**
     * Gets the all methods in hierarchy.
     * @param clazz the class
     * @return the all methods in hierarchy
     */
    public Method[] getAllMethodsInHierarchy(final Class<?> clazz) {
        return getAllMethodsInHierarchy(clazz, false);
    }

    /**
     * Gets the all methods in hierarchy.
     * @param clazz        the class
     * @param concreteOnly the concrete only flag
     * @return the all methods in hierarchy
     */
    public Method[] getAllMethodsInHierarchy(final Class<?> clazz, final boolean concreteOnly) {
        final Set<Method> result = new LinkedHashSet<>();

        addAllMethods(clazz.getDeclaredMethods(), concreteOnly, result);
        addAllMethods(clazz.getMethods(), concreteOnly, result);

        if (clazz.getSuperclass() != null) {
            addAllMethods(getAllMethodsInHierarchy(clazz.getSuperclass(), concreteOnly), concreteOnly, result);
        }

        return result.toArray(new Method[result.size()]);
    }

    /**
     * Gets the method by name.
     * @param clazz         the class
     * @param name          the name
     * @param caseSensitive the case sensitive
     * @param concreteOnly  the concrete only
     * @param parameters    the parameters
     * @return the method by name
     */
    public Method getMethod(final Class<?> clazz, final String name, final boolean caseSensitive, final boolean concreteOnly, final Class<?>... parameters) {
        for (final Method method : getAllMethodsInHierarchy(clazz, concreteOnly)) {
            if ((caseSensitive && method.getName().equals(name) || method.getName().equalsIgnoreCase(name)) && Arrays.equals(parameters, method.getParameterTypes())) {
                return method;
            }
        }

        return null;
    }

    /**
     * Gets the method by name.
     * @param clazz the class
     * @param name  the name
     * @return the method by name
     */
    public Method getMethodByName(final Class<?> clazz, final String name) {
        return getMethodByName(clazz, name, true, false);
    }

    /**
     * Gets the method by name.
     * @param clazz         the class
     * @param name          the name
     * @param caseSensitive the case sensitive flag
     * @param concreteOnly  the concrete only flag
     * @return the method by name
     */
    public Method getMethodByName(final Class<?> clazz, final String name, final boolean caseSensitive, final boolean concreteOnly) {
        for (final Method method : getAllMethodsInHierarchy(clazz, concreteOnly)) {
            if (caseSensitive && method.getName().equals(name) || method.getName().equalsIgnoreCase(name)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Gets the primitive map.
     * @return the primitive map
     */
    public BidiMap<Class<?>, Class<?>> getPrimitiveMap() {
        return primitiveMap;
    }

    /**
     * Checks if is assignable.
     * @param from the from
     * @param to   the to
     * @return true, if is assignable
     */
    public boolean isAssignable(final Class<?> from, final Class<?> to) {
        Class<?> left = from;
        Class<?> right = to;

        if (from.isPrimitive()) {
            left = primitiveMap.get(from);
        }

        if (to.isPrimitive()) {
            right = primitiveMap.get(to);
        }

        if (ClassUtils.isAssignable(left, right, true)) {
            return true;
        }

        if (Number.class.isAssignableFrom(right) && Number.class.isAssignableFrom(left)) {
            return NumberUtils.getMaximumValue(left) > NumberUtils.getMaximumValue(right);
        }

        return false;
    }
}

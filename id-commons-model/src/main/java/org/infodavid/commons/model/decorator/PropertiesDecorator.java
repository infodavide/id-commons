package org.infodavid.commons.model.decorator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.infodavid.commons.model.Constants;
import org.infodavid.commons.model.EntityProperty;
import org.infodavid.commons.model.PropertyType;

import lombok.Getter;
import lombok.experimental.Delegate;

/**
 * The Class PropertiesDecorator.
 */
public class PropertiesDecorator implements Set<EntityProperty>, Serializable {

    /** The Constant EMPTY. */
    public static final PropertiesDecorator EMPTY = new PropertiesDecorator(Collections.emptySet()); // NOSONAR Immutable

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6168960167743665458L;

    /** The delegate. */
    @Delegate
    @Getter
    private final Set<EntityProperty> delegate;

    /**
     * Instantiates a new decorator.
     */
    public PropertiesDecorator() {
        delegate = new HashSet<>();
    }

    /**
     * Instantiates a new decorator.
     * @param delegate the delegate
     */
    public PropertiesDecorator(final Set<EntityProperty> delegate) {
        this.delegate = delegate;
    }

    /**
     * Instantiates a new decorator.
     * @param delegate the delegate
     * @param source   the source
     */
    public PropertiesDecorator(final Set<EntityProperty> delegate, final PropertiesDecorator source) {
        this(delegate);

        for (final EntityProperty property : source) {
            add(new EntityProperty(property));
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(final EntityProperty property) {
        if (property == null || StringUtils.isEmpty(property.getName())) {
            return false;
        }

        if (StringUtils.isEmpty(property.getScope())) {
            property.setScope(null);
        }

        // ensure the value is not present to add it
        delegate.remove(property);

        return delegate.add(property);
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     */
    public void add(final String scope, final String name, final boolean value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);

        if (property == null) {
            property = new EntityProperty(s, name, PropertyType.BOOLEAN, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     */
    public void add(final String scope, final String name, final double value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);

        if (property == null) {
            property = new EntityProperty(s, name, PropertyType.DOUBLE, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Put.
     * @param scope the scope
     * @param name  the name
     * @param value the value
     * @return the property
     */
    public EntityProperty add(final String scope, final String name, final EntityProperty value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        final EntityProperty previous = remove(scope, name);

        if (value != null) {
            String s = scope;

            if (StringUtils.isEmpty(s)) {
                s = null;
            }

            final EntityProperty property = new EntityProperty(value);
            property.setScope(s);
            property.setName(name);
            add(property);
        }

        return previous;
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     */
    public void add(final String scope, final String name, final float value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);

        if (property == null) {
            property = new EntityProperty(s, name, PropertyType.DOUBLE, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     */
    public void add(final String scope, final String name, final int value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);

        if (property == null) {
            property = new EntityProperty(s, name, PropertyType.INTEGER, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     */
    public void add(final String scope, final String name, final long value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);

        if (property == null) {
            property = new EntityProperty(s, name, PropertyType.INTEGER, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            property.setValue(value);
        }
    }

    /**
     * Sets the property with STRING type.
     * @param scope the scope
     * @param name  the new property name
     * @param value the new property value
     * @return the replaced object or null
     */
    public Object add(final String scope, final String name, final Object value){
        return add(scope, name, PropertyType.STRING, value);
    }

    /**
     * Sets the property.
     * @param scope the scope
     * @param name  the new property name
     * @param type  the type
     * @param value the new property value
     * @return the replaced object or null
     */
    public Object add(final String scope, final String name, final PropertyType type, final Object value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);
        Object previousObject = null;

        if (property == null) {
            property = new EntityProperty(s, name, type, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            previousObject = property.getObject();

            if (value instanceof final EntityProperty p) {
                property.setValue(p.getValue());
            } else {
                property.setValue(value);
            }
        }

        return previousObject;
    }

    public Object add(final String scope, final String name, final PropertyType type, final String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Constants.NAME_MUST_NOT_BE_NULL_OR_EMPTY);
        }

        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        EntityProperty property = get(s, name);
        Object previousObject = null;

        if (property == null) {
            property = new EntityProperty(s, name, type, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            previousObject = property.getValue();
            property.setValue(value);

        }

        return previousObject;
    }

    public Object add(final String scope, final String name, final String value) {
        return add(scope, name, PropertyType.STRING, value);
    }

    /*
     * (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends EntityProperty> c) {
        boolean result = true;

        for (final EntityProperty property : c) {
            result = result && add(property);
        }

        return result;
    }

    /**
     * Contains.
     * @param scope the scope
     * @param name  the name
     * @return true, if successful
     */
    public boolean contains(final String scope, final String name) {
        return get(scope, name) != null;
    }

    /**
     * Contains property.
     * @param name the name
     * @return true, if successful
     */
    public boolean containsProperty(final String name) {
        return delegate.stream().filter(p -> Objects.equals(p.getName(), name)).findFirst().orElse(null) != null;
    }

    /**
     * Contains scope.
     * @param scope the scope
     * @return true, if successful
     */
    public boolean containsScope(final String scope) {
        final String s;

        if (StringUtils.isEmpty(scope)) {
            s = null;
        } else {
            s = scope;
        }

        return delegate.stream().filter(p -> Objects.equals(p.getScope(), s)).findFirst().orElse(null) != null;
    }

    /**
     * Gets the objects of the properties associated to the given scope.
     * @param scope the name of the scope
     * @return the values
     */
    public Map<String, Object> get(final String scope) {
        final Map<String, Object> result = new HashMap<>();
        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        for (final EntityProperty property : delegate) {
            if (Objects.equals(property.getScope(), s)) {
                result.put(property.getName(), property.getObject());
            }
        }

        return result;
    }

    /**
     * Gets the.
     * @param scope the scope
     * @param name  the name
     * @return the property
     */
    public EntityProperty get(final String scope, final String name) {
        final String s;

        if (StringUtils.isEmpty(scope)) {
            s = null;
        } else {
            s = scope;
        }

        return delegate.stream().filter(p -> Objects.equals(p.getScope(), s) && Objects.equals(p.getName(), name)).findFirst().orElse(null);
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public boolean getOrDefault(final String scope, final String name, final boolean defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public double getOrDefault(final String scope, final String name, final double defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the property or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public EntityProperty getOrDefault(final String scope, final String name, final EntityProperty defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property;
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public int getOrDefault(final String scope, final String name, final int defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public long getOrDefault(final String scope, final String name, final long defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public Object getOrDefault(final String scope, final String name, final Object defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the value or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public String getOrDefault(final String scope, final String name, final String defaultValue) {
        final EntityProperty property = get(scope, name);

        return property == null ? defaultValue : property.getValueOrDefault(defaultValue);
    }

    /**
     * Gets the properties associated to the given scope.
     * @param scope the name of the scope
     * @return the properties
     */
    public Map<String, EntityProperty> getProperties(final String scope) {
        final Map<String, EntityProperty> result = new HashMap<>();
        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        for (final EntityProperty property : delegate) {
            if (Objects.equals(property.getScope(), s)) {
                result.put(property.getName(), property);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o) {
        if (o instanceof EntityProperty) {
            remove(o);
        }

        return false;
    }

    /**
     * Removes the.
     * @param scope the scope
     * @param name  the name
     * @return the property
     */
    public EntityProperty remove(final String scope, final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        final Iterator<EntityProperty> ite = delegate.iterator();

        while (ite.hasNext()) {
            final EntityProperty property = ite.next();

            if (Objects.equals(property.getScope(), s) && Objects.equals(property.getName(), name)) {
                ite.remove();

                return property;
            }
        }

        return null;
    }

    /**
     * Removes the.
     * @param scope the scope
     * @param name  the name
     * @param value the value
     * @return the property
     */
    public EntityProperty remove(final String scope, final String name, final Object value) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        final Iterator<EntityProperty> ite = delegate.iterator();

        while (ite.hasNext()) {
            final EntityProperty property = ite.next();

            if (Objects.equals(property.getScope(), s) && Objects.equals(property.getName(), name) && Objects.equals(property.getObject(), value)) {
                ite.remove();

                return property;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() { // NOSONAR No complexity
        if (delegate.isEmpty()) {
            return StringUtils.EMPTY;
        }

        final StringBuilder buffer = new StringBuilder();

        for (final Entry<String, EntityProperty> propertyEntry : getProperties(null).entrySet()) {
            buffer.append(propertyEntry.getKey());
            buffer.append('=');

            if (propertyEntry.getValue() == null) {
                buffer.append("<null>");
            } else if (propertyEntry.getValue().getValue() != null && PropertyType.PASSWORD.equals(propertyEntry.getValue().getType())) {
                buffer.append(StringUtils.repeat('*', propertyEntry.getValue().getValue().length()));
            } else {
                buffer.append(propertyEntry.getValue().getValue());
            }

            buffer.append(" (");
            buffer.append(propertyEntry.getValue().getType());
            buffer.append(")\n");
        }

        final Set<String> scopes = new TreeSet<>();

        for (final EntityProperty property : delegate) {
            if (StringUtils.isNotEmpty(property.getScope())) {
                scopes.add(property.getScope());
            }
        }

        for (final String scope : scopes) {
            buffer.append(scope);
            buffer.append(":\n");

            for (final Entry<String, EntityProperty> propertyEntry : getProperties(scope).entrySet()) {
                buffer.append('\t');
                buffer.append(propertyEntry.getKey());
                buffer.append('=');

                if (propertyEntry.getValue() == null) {
                    buffer.append("<null>");
                } else if (propertyEntry.getValue().getValue() != null && PropertyType.PASSWORD.equals(propertyEntry.getValue().getType())) {
                    buffer.append(StringUtils.repeat('*', propertyEntry.getValue().getValue().length()));
                } else {
                    buffer.append(propertyEntry.getValue().getValue());
                }

                buffer.append(" (");
                buffer.append(propertyEntry.getValue().getType());
                buffer.append(")\n");
            }
        }

        return buffer.toString();
    }
}

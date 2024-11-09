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
import org.infodavid.commons.model.Property;
import org.infodavid.commons.model.PropertyType;

import lombok.Getter;
import lombok.experimental.Delegate;

/**
 * The Class PropertiesDecorator.
 */
public class PropertiesDecorator implements Set<Property>, Serializable {

    /** The Constant EMPTY. */
    public static final PropertiesDecorator EMPTY = new PropertiesDecorator(Collections.emptySet()); // NOSONAR Immutable

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6168960167743665458L;

    /** The delegate. */
    @Delegate
    @Getter
    private final Set<Property> delegate;

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
    public PropertiesDecorator(final Set<Property> delegate) {
        this.delegate = delegate;
    }

    /**
     * Instantiates a new decorator.
     * @param delegate the delegate
     * @param source   the source
     */
    public PropertiesDecorator(final Set<Property> delegate, final PropertiesDecorator source) {
        this(delegate);

        for (final Property property : source) {
            add(new Property(property));
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(final Property property) {
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
            return;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);

        if (property == null) {
            property = new Property(s, name, PropertyType.BOOLEAN, String.valueOf(value));
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
            return;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);

        if (property == null) {
            property = new Property(s, name, PropertyType.DOUBLE, String.valueOf(value));
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
    public void add(final String scope, final String name, final float value) {
        if (StringUtils.isEmpty(name)) {
            return;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);

        if (property == null) {
            property = new Property(s, name, PropertyType.DOUBLE, String.valueOf(value));
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
            return;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);

        if (property == null) {
            property = new Property(s, name, PropertyType.INTEGER, String.valueOf(value));
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
            return;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);

        if (property == null) {
            property = new Property(s, name, PropertyType.INTEGER, String.valueOf(value));
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
     * @return the replaced object or null
     */
    public Object add(final String scope, final String name, final Object value) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        Property property = get(s, name);
        Object previousObject = null;

        if (property == null) {
            property = new Property(s, name, PropertyType.STRING, String.valueOf(value));
            // ensure the value is not present to add it
            delegate.remove(property);
            delegate.add(property);
        } else {
            previousObject = property.getObject();

            if (value instanceof final Property p) {
                property.setValue(p.getValue());
            } else {
                property.setValue(value);
            }
        }

        return previousObject;
    }

    /**
     * Put.
     * @param scope the scope
     * @param name  the name
     * @param value the value
     * @return the property
     */
    public Property add(final String scope, final String name, final Property value) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        final Property previous = remove(scope, name);

        if (value != null) {
            String s = scope;

            if (StringUtils.isEmpty(s)) {
                s = null;
            }

            final Property property = new Property(value);
            property.setScope(s);
            property.setName(name);
            add(property);
        }

        return previous;
    }

    /*
     * (non-Javadoc)
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends Property> c) {
        boolean result = true;

        for (final Property property : c) {
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

        for (final Property property : delegate) {
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
    public Property get(final String scope, final String name) {
        final String s;

        if (StringUtils.isEmpty(scope)) {
            s = null;
        } else {
            s = scope;
        }

        return delegate.stream().filter(p -> Objects.equals(p.getScope(), s) && Objects.equals(p.getName(), name)).findFirst().orElse(null);
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public boolean getOrDefault(final String scope, final String name, final boolean defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : Boolean.parseBoolean(property.getValue());
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public double getOrDefault(final String scope, final String name, final double defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : Double.parseDouble(property.getValue());
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public int getOrDefault(final String scope, final String name, final int defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : Integer.parseInt(property.getValue());
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public long getOrDefault(final String scope, final String name, final long defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : Long.parseLong(property.getValue());
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public Object getOrDefault(final String scope, final String name, final Object defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : property.getObject();
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public Property getOrDefault(final String scope, final String name, final Property defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : property;
    }

    /**
     * Gets the or default.
     * @param scope        the scope
     * @param name         the name
     * @param defaultValue the default value
     * @return the or default
     */
    public String getOrDefault(final String scope, final String name, final String defaultValue) {
        final Property property = get(scope, name);

        return property == null ? defaultValue : property.getValue();
    }

    /**
     * Gets the properties associated to the given scope.
     * @param scope the name of the scope
     * @return the properties
     */
    public Map<String, Property> getProperties(final String scope) {
        final Map<String, Property> result = new HashMap<>();
        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        for (final Property property : delegate) {
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
        if (o instanceof Property) {
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
    public Property remove(final String scope, final String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        final Iterator<Property> ite = delegate.iterator();

        while (ite.hasNext()) {
            final Property property = ite.next();

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
    public Property remove(final String scope, final String name, final Object value) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        String s = scope;

        if (StringUtils.isEmpty(s)) {
            s = null;
        }

        final Iterator<Property> ite = delegate.iterator();

        while (ite.hasNext()) {
            final Property property = ite.next();

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
    public String toString() { //NOSONAR No complexity
        if (delegate.isEmpty()) {
            return StringUtils.EMPTY;
        }

        final StringBuilder buffer = new StringBuilder();

        for (final Entry<String, Property> propertyEntry : getProperties(null).entrySet()) {
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

        for (final Property property : delegate) {
            if (StringUtils.isNotEmpty(property.getScope())) {
                scopes.add(property.getScope());
            }
        }

        for (final String scope : scopes) {
            buffer.append(scope);
            buffer.append(":\n");

            for (final Entry<String, Property> propertyEntry : getProperties(scope).entrySet()) {
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

package org.infodavid.commons.test.mockito;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import lombok.Locked;
import lombok.NoArgsConstructor;

/**
 * The Class BasicAnswer.
 * @param <T> the generic type
 */
@NoArgsConstructor
public class BasicAnswer<T> implements Answer<T> {

    /** The value. */
    private T value;

    /**
     * Instantiates a new answer.
     * @param value the value
     */
    public BasicAnswer(final T value) {
        this.value = value;
    }

    /*
     * (non-javadoc)
     * @see org.mockito.stubbing.Answer#answer(org.mockito.invocation.InvocationOnMock)
     */
    @Override
    @Locked.Read
    public T answer(final InvocationOnMock invocation) throws Throwable {
        return value;
    }

    /**
     * Gets the value.
     * @return the value
     */
    @Locked.Read
    public T getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value the new value
     */
    @Locked.Write
    public void setValue(final T value) {
        this.value = value;
    }
}

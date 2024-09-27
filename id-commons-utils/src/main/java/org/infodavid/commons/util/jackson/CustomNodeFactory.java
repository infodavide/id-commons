package org.infodavid.commons.util.jackson;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * A factory for creating CustomNode objects.
 */
class CustomNodeFactory extends JsonNodeFactory {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6378950786847793844L;

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(long)
     */
    @Override
    public NumericNode numberNode(final long v) {
        return LongNode.valueOf(v);
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(java.lang.Long)
     */
    @Override
    public ValueNode numberNode(final Long v) {
        return LongNode.valueOf(v.longValue());
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(int)
     */
    @Override
    public NumericNode numberNode(final int v) {
        return LongNode.valueOf(v);
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(java.lang.Integer)
     */
    @Override
    public ValueNode numberNode(final Integer v) {
        return LongNode.valueOf(v.longValue());
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(java.lang.Byte)
     */
    @Override
    public ValueNode numberNode(final Byte v) {
        return LongNode.valueOf(v.longValue());
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(byte)
     */
    @Override
    public NumericNode numberNode(final byte v) {
        return LongNode.valueOf(v);
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(short)
     */
    @Override
    public NumericNode numberNode(final short v) {
        return LongNode.valueOf(v);
    }

    /*
     * (non-Javadoc)
     * @see com.fasterxml.jackson.databind.node.JsonNodeFactory#numberNode(java.lang.Short)
     */
    @Override
    public ValueNode numberNode(final Short v) {
        return LongNode.valueOf(v.longValue());
    }
}

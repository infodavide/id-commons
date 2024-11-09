package org.infodavid.commons.converter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The Class StringUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("boxing")
class ConverterUtilsTest extends TestCase {

    /**
     * Test to booleans.
     * @throws Exception the exception
     */
    @Test
    void testToBooleans() throws Exception {
        assertArrayEquals(new boolean[] { true, false, true, false, true, false, true, false }, ConverterUtils.toBooleans("[true,false,yes,no,on,off,1,0]"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanUsingBoolean() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean(Boolean.TRUE), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanUsingInteger() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean(Integer.valueOf(1)), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithFalse() throws Exception {
        assertEquals(false, ConverterUtils.toBoolean("false"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithNo() throws Exception {
        assertEquals(false, ConverterUtils.toBoolean("no"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithOff() throws Exception {
        assertEquals(false, ConverterUtils.toBoolean("off"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithOn() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean("on"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithTrue() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean("true"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithUsingInteger0() throws Exception {
        assertEquals(false, ConverterUtils.toBoolean(Integer.valueOf(0)), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithUsingInteger1() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean(Integer.valueOf(1)), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithUsingString0() throws Exception {
        assertEquals(false, ConverterUtils.toBoolean("0"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithUsingString1() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean("1"), "Wrong result");
    }

    /**
     * Test to boolean.
     * @throws Exception the exception
     */
    @Test
    void testToBooleanWithYes() throws Exception {
        assertEquals(true, ConverterUtils.toBoolean("yes"), "Wrong result");
    }

    /**
     * Test to bytes.
     * @throws Exception the exception
     */
    @Test
    void testToBytes() throws Exception {
        assertArrayEquals(new byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE, 0 }, ConverterUtils.toBytes("[" + Byte.MIN_VALUE + ',' + Byte.MAX_VALUE + ",0]"), "Wrong result");
    }

    /**
     * Test to byte.
     * @throws Exception the exception
     */
    @Test
    void testToByteUsingByte() throws Exception {
        assertEquals((byte) 1, ConverterUtils.toByte(Byte.valueOf((byte) 1)), "Wrong result");
    }

    /**
     * Test to byte.
     * @throws Exception the exception
     */
    @Test
    void testToByteUsingHexString() throws Exception {
        assertEquals((byte) 127, ConverterUtils.toByte("0x7F"), "Wrong result");
    }

    /**
     * Test to byte.
     * @throws Exception the exception
     */
    @Test
    void testToByteUsingInteger() throws Exception {
        assertEquals((byte) 1, ConverterUtils.toByte(Integer.valueOf(1)), "Wrong result");
    }

    /**
     * Test to byte.
     * @throws Exception the exception
     */
    @Test
    void testToByteUsingString() throws Exception {
        assertEquals((byte) 1, ConverterUtils.toByte("1"), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToDateUsingDate() throws Exception {
        assertEquals(Date.from(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")).toInstant()), ConverterUtils.toDate(Date.from(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")).toInstant())), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToDateUsingLong() throws Exception {
        assertEquals(Date.from(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")).toInstant()), ConverterUtils.toDate(Date.from(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")).toInstant()).getTime()), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToDateUsingString() throws Exception {
        assertEquals(Date.from(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")).toInstant()), ConverterUtils.toDate("20230123 01:02:03.000Z"), "Wrong result");
    }

    /**
     * Test to doubles.
     * @throws Exception the exception
     */
    @Test
    void testToDoubles() throws Exception {
        assertArrayEquals(new double[] { Double.MIN_VALUE, Double.MAX_VALUE, 0.0, 1.1 }, ConverterUtils.toDoubles("[" + Double.MIN_VALUE + ',' + Double.MAX_VALUE + ",0,1.1]"), 0.0, "Wrong result");
    }

    /**
     * Test to double.
     * @throws Exception the exception
     */
    @Test
    void testToDoubleUsingDouble() throws Exception {
        assertEquals(1.1, ConverterUtils.toDouble(Double.valueOf(1.1)), 0.0, "Wrong result");
    }

    /**
     * Test to double.
     * @throws Exception the exception
     */
    @Test
    void testToDoubleUsingFloat() throws Exception {
        assertEquals(1.1, ConverterUtils.toDouble(Float.valueOf(1.1f)), 0.0, "Wrong result");
    }

    /**
     * Test to double.
     * @throws Exception the exception
     */
    @Test
    void testToDoubleUsingInteger() throws Exception {
        assertEquals(1.0, ConverterUtils.toDouble(Integer.valueOf(1)), 0.0, "Wrong result");
    }

    /**
     * Test to double.
     * @throws Exception the exception
     */
    @Test
    void testToDoubleUsingString() throws Exception {
        assertEquals(1.1, ConverterUtils.toDouble("1.1"), 0.0, "Wrong result");
    }

    /**
     * Test to floats.
     * @throws Exception the exception
     */
    @Test
    void testToFloats() throws Exception {
        assertArrayEquals(new float[] { Float.MIN_VALUE, Float.MAX_VALUE, 0.0f, 1.1f }, ConverterUtils.toFloats("[" + Float.MIN_VALUE + ',' + Float.MAX_VALUE + ",0,1.1]"), 0.0f, "Wrong result");
    }

    /**
     * Test to float.
     * @throws Exception the exception
     */
    @Test
    void testToFloatUsingDouble() throws Exception {
        assertEquals(1.1f, ConverterUtils.toFloat(Double.valueOf(1.1f)), 0.0f, "Wrong result");
    }

    /**
     * Test to float.
     * @throws Exception the exception
     */
    @Test
    void testToFloatUsingFloat() throws Exception {
        assertEquals(1.1f, ConverterUtils.toFloat(Float.valueOf(1.1f)), 0.0f, "Wrong result");
    }

    /**
     * Test to float.
     * @throws Exception the exception
     */
    @Test
    void testToFloatUsingInteger() throws Exception {
        assertEquals(1.0f, ConverterUtils.toFloat(Integer.valueOf(1)), 0.0f, "Wrong result");
    }

    /**
     * Test to float.
     * @throws Exception the exception
     */
    @Test
    void testToFloatUsingString() throws Exception {
        assertEquals(1.1f, ConverterUtils.toFloat("1.1"), 0.0f, "Wrong result");
    }

    /**
     * Test to integers.
     * @throws Exception the exception
     */
    @Test
    void testToIntegers() throws Exception {
        assertArrayEquals(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1 }, ConverterUtils.toIntegers("[" + Integer.MIN_VALUE + ',' + Integer.MAX_VALUE + ",0,1]"), "Wrong result");
    }

    /**
     * Test to integer.
     * @throws Exception the exception
     */
    @Test
    void testToIntegerUsingDouble() throws Exception {
        assertEquals(1, ConverterUtils.toInteger(Double.valueOf(1.1)), "Wrong result");
    }

    /**
     * Test to integer.
     * @throws Exception the exception
     */
    @Test
    void testToIntegerUsingHexString() throws Exception {
        assertEquals(129, ConverterUtils.toInteger("0x81"), "Wrong result");
    }

    /**
     * Test to integer.
     * @throws Exception the exception
     */
    @Test
    void testToIntegerUsingInteger() throws Exception {
        assertEquals(1, ConverterUtils.toInteger(Integer.valueOf(1)), "Wrong result");
    }

    /**
     * Test to integer.
     * @throws Exception the exception
     */
    @Test
    void testToIntegerUsingString() throws Exception {
        assertEquals(1, ConverterUtils.toInteger("1"), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToLocalDateTimeUsingDate() throws Exception {
        assertEquals(LocalDateTime.of(2023, 1, 23, 1, 2, 3), ConverterUtils.toObject(LocalDateTime.of(2023, 1, 23, 1, 2, 3), LocalDateTime.class), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToLocalDateTimeUsingString() throws Exception {
        assertEquals(LocalDateTime.of(2023, 1, 23, 1, 2, 3), ConverterUtils.toObject("20230123 01:02:03.000", LocalDateTime.class), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToLocalDateUsingDate() throws Exception {
        assertEquals(LocalDate.of(2023, 1, 23), ConverterUtils.toObject(LocalDate.of(2023, 1, 23), LocalDate.class), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToLocalDateUsingString() throws Exception {
        assertEquals(LocalDate.of(2023, 1, 23), ConverterUtils.toObject("20230123", LocalDate.class), "Wrong result");
    }

    /**
     * Test to longs.
     * @throws Exception the exception
     */
    @Test
    void testToLongs() throws Exception {
        assertArrayEquals(new long[] { Long.MIN_VALUE, Long.MAX_VALUE, 0, 1 }, ConverterUtils.toLongs("[" + Long.MIN_VALUE + ',' + Long.MAX_VALUE + ",0,1]"), "Wrong result");
    }

    /**
     * Test to long.
     * @throws Exception the exception
     */
    @Test
    void testToLongUsingDouble() throws Exception {
        assertEquals(1L, ConverterUtils.toLong(Double.valueOf(1.1)), "Wrong result");
    }

    /**
     * Test to long.
     * @throws Exception the exception
     */
    @Test
    void testToLongUsingLong() throws Exception {
        assertEquals(1L, ConverterUtils.toLong(Long.valueOf(1)), "Wrong result");
    }

    /**
     * Test to long.
     * @throws Exception the exception
     */
    @Test
    void testToLongUsingString() throws Exception {
        assertEquals(1L, ConverterUtils.toLong("1"), "Wrong result");
    }

    /**
     * Test to shorts.
     * @throws Exception the exception
     */
    @Test
    void testToShorts() throws Exception {
        assertArrayEquals(new short[] { Short.MIN_VALUE, Short.MAX_VALUE, 0, 1 }, ConverterUtils.toShorts("[" + Short.MIN_VALUE + ',' + Short.MAX_VALUE + ",0,1]"), "Wrong result");
    }

    /**
     * Test to short.
     * @throws Exception the exception
     */
    @Test
    void testToShortUsingDouble() throws Exception {
        assertEquals((short) 1, ConverterUtils.toShort(Double.valueOf(1.1)), "Wrong result");
    }

    /**
     * Test to short.
     * @throws Exception the exception
     */
    @Test
    void testToShortUsingHexString() throws Exception {
        assertEquals((short) 129, ConverterUtils.toShort("0x81"), "Wrong result");
    }

    /**
     * Test to short.
     * @throws Exception the exception
     */
    @Test
    void testToShortUsingShort() throws Exception {
        assertEquals((short) 1, ConverterUtils.toShort(Short.valueOf((short) 1)), "Wrong result");
    }

    /**
     * Test to short.
     * @throws Exception the exception
     */
    @Test
    void testToShortUsingString() throws Exception {
        assertEquals((short) 1, ConverterUtils.toShort("1"), "Wrong result");
    }

    /**
     * Test to string boolean array.
     * @throws Exception the exception
     */
    @Test
    void testToStringBooleanArray() throws Exception {
        assertEquals("[true,false]", ConverterUtils.toString(new boolean[] { true, false }));
    }

    /**
     * Test to string boolean object array.
     * @throws Exception the exception
     */
    @Test
    void testToStringBooleanObjectArray() throws Exception {
        assertEquals("[true,false]", ConverterUtils.toString(new Boolean[] { Boolean.valueOf(true), Boolean.valueOf(false) }));
    }

    /**
     * Test to string boolean object list.
     * @throws Exception the exception
     */
    @Test
    void testToStringBooleanObjectList() throws Exception {
        assertEquals("[true,false]", ConverterUtils.toString(Arrays.asList(Boolean.valueOf(true), Boolean.valueOf(false))));
    }

    /**
     * Test to string boolean object map.
     * @throws Exception the exception
     */
    @Test
    void testToStringBooleanObjectMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", Boolean.valueOf(true));
        map.put("2", Boolean.valueOf(false));

        assertEquals("{1=true,2=false}", ConverterUtils.toString(map));
    }

    /**
     * Test to string byte array.
     * @throws Exception the exception
     */
    @Test
    void testToStringByteArray() throws Exception {
        assertEquals("[1,2,3," + Byte.MIN_VALUE + ',' + Byte.MAX_VALUE + "]", ConverterUtils.toString(new byte[] { 1, 2, 3, Byte.MIN_VALUE, Byte.MAX_VALUE }));
    }

    /**
     * Test to string byte object array.
     * @throws Exception the exception
     */
    @Test
    void testToStringByteObjectArray() throws Exception {
        assertEquals("[1,2,3," + Byte.MIN_VALUE + ',' + Byte.MAX_VALUE + "]", ConverterUtils.toString(new Byte[] { Byte.valueOf((byte) 1), Byte.valueOf((byte) 2), Byte.valueOf((byte) 3), Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE) }));
    }

    /**
     * Test to string byte object list.
     * @throws Exception the exception
     */
    @Test
    void testToStringByteObjectList() throws Exception {
        assertEquals("[1,2,3]", ConverterUtils.toString(Arrays.asList(Byte.valueOf((byte) 1), Byte.valueOf((byte) 2), Byte.valueOf((byte) 3))));
    }

    /**
     * Test to string byte object map.
     * @throws Exception the exception
     */
    @Test
    void testToStringByteObjectMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", Byte.valueOf((byte) 1));
        map.put("2", Byte.valueOf((byte) 2));
        map.put("3", Byte.valueOf((byte) 3));

        assertEquals("{1=1,2=2,3=3}", ConverterUtils.toString(map), "Wrong result");
    }

    /**
     * Test to string double array.
     * @throws Exception the exception
     */
    @Test
    void testToStringDoubleArray() throws Exception {
        assertEquals("[1.1,2.1,3.1," + Double.MIN_VALUE + ',' + Double.MAX_VALUE + "]", ConverterUtils.toString(new double[] { 1.1, 2.1, 3.1, Double.MIN_VALUE, Double.MAX_VALUE }), "Wrong result");
    }

    /**
     * Test to string double object array.
     * @throws Exception the exception
     */
    @Test
    void testToStringDoubleObjectArray() throws Exception {
        assertEquals("[1.1,2.1,3.1," + Double.MIN_VALUE + ',' + Double.MAX_VALUE + "]", ConverterUtils.toString(new Double[] { Double.valueOf(1.1), Double.valueOf(2.1), Double.valueOf(3.1), Double.valueOf(Double.MIN_VALUE), Double.valueOf(Double.MAX_VALUE) }), "Wrong result");
    }

    /**
     * Test to string double object list.
     * @throws Exception the exception
     */
    @Test
    void testToStringDoubleObjectList() throws Exception {
        assertEquals("[1.1,2.1,3.1]", ConverterUtils.toString(Arrays.asList(Double.valueOf(1.1), Double.valueOf(2.1), Double.valueOf(3.1))), "Wrong result");
    }

    /**
     * Test to string double object map.
     * @throws Exception the exception
     */
    @Test
    void testToStringDoubleObjectMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", Double.valueOf(1));
        map.put("2", Double.valueOf(2));
        map.put("3", Double.valueOf(3));

        assertEquals("{1=1.0,2=2.0,3=3.0}", ConverterUtils.toString(map));
    }

    /**
     * Test to string integer array.
     * @throws Exception the exception
     */
    @Test
    void testToStringIntArray() throws Exception {
        assertEquals("[1,2,3," + Integer.MIN_VALUE + ',' + Integer.MAX_VALUE + "]", ConverterUtils.toString(new int[] { 1, 2, 3, Integer.MIN_VALUE, Integer.MAX_VALUE }), "Wrong result");
    }

    /**
     * Test to string integer object array.
     * @throws Exception the exception
     */
    @Test
    void testToStringIntegerObjectArray() throws Exception {
        assertEquals("[1,2,3," + Integer.MIN_VALUE + ',' + Integer.MAX_VALUE + "]", ConverterUtils.toString(new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE) }), "Wrong result");
    }

    /**
     * Test to string integer object list.
     * @throws Exception the exception
     */
    @Test
    void testToStringIntegerObjectList() throws Exception {
        assertEquals("[1,2,3]", ConverterUtils.toString(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3))), "Wrong result");
    }

    /**
     * Test to string integer object map.
     * @throws Exception the exception
     */
    @Test
    void testToStringIntegerObjectMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", Integer.valueOf(1));
        map.put("2", Integer.valueOf(2));
        map.put("3", Integer.valueOf(3));

        assertEquals("{1=1,2=2,3=3}", ConverterUtils.toString(map), "Wrong result");
    }

    /**
     * Test to string long array.
     * @throws Exception the exception
     */
    @Test
    void testToStringLongArray() throws Exception {
        assertEquals("[1,2,3," + Long.MIN_VALUE + ',' + Long.MAX_VALUE + "]", ConverterUtils.toString(new long[] { 1, 2, 3, Long.MIN_VALUE, Long.MAX_VALUE }), "Wrong result");
    }

    /**
     * Test to string long object array.
     * @throws Exception the exception
     */
    @Test
    void testToStringLongObjectArray() throws Exception {
        assertEquals("[1,2,3," + Long.MIN_VALUE + ',' + Long.MAX_VALUE + "]", ConverterUtils.toString(new Long[] { Long.valueOf(1), Long.valueOf(2), Long.valueOf(3), Long.valueOf(Long.MIN_VALUE), Long.valueOf(Long.MAX_VALUE) }), "Wrong result");
    }

    /**
     * Test to string long object list.
     * @throws Exception the exception
     */
    @Test
    void testToStringLongObjectList() throws Exception {
        assertEquals("[1,2,3]", ConverterUtils.toString(Arrays.asList(Long.valueOf(1), Long.valueOf(2), Long.valueOf(3))), "Wrong result");
    }

    /**
     * Test to string long object map.
     * @throws Exception the exception
     */
    @Test
    void testToStringLongObjectMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", Long.valueOf(1));
        map.put("2", Long.valueOf(2));
        map.put("3", Long.valueOf(3));

        assertEquals("{1=1,2=2,3=3}", ConverterUtils.toString(map), "Wrong result");
    }

    /**
     * Test to string string array.
     * @throws Exception the exception
     */
    @Test
    void testToStringStringArray() throws Exception {
        assertEquals("[1,2,3]", ConverterUtils.toString(new String[] { String.valueOf(1), String.valueOf(2), String.valueOf(3) }), "Wrong result");
    }

    /**
     * Test to string string list.
     * @throws Exception the exception
     */
    @Test
    void testToStringStringList() throws Exception {
        assertEquals("[1,2,3]", ConverterUtils.toString(Arrays.asList(String.valueOf(1), String.valueOf(2), String.valueOf(3))), "Wrong result");
    }

    /**
     * Test to string string map.
     * @throws Exception the exception
     */
    @Test
    void testToStringStringMap() throws Exception {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");

        assertEquals("{1=1,2=2,3=3}", ConverterUtils.toString(map));
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToZonedDateTimeUsingDate() throws Exception {
        assertEquals(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")), ConverterUtils.toObject(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("UTC")), ZonedDateTime.class), "Wrong result");
    }

    /**
     * Test to date.
     * @throws Exception the exception
     */
    @Test
    void testToZonedDateTimeUsingString() throws Exception {
        assertEquals(ZonedDateTime.of(2023, 1, 23, 1, 2, 3, 0, ZoneId.of("Z")), ConverterUtils.toObject("20230123 01:02:03.000Z", ZonedDateTime.class), "Wrong result");
    }
}

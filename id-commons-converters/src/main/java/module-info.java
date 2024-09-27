module org.infodavid.commons.converter {
    exports org.infodavid.commons.converter;

    provides org.infodavid.commons.converter.ConverterWithDefaultType with
    org.infodavid.commons.converter.ByteConverter,
    org.infodavid.commons.converter.CalendarConverter,
    org.infodavid.commons.converter.DateConverter,
    org.infodavid.commons.converter.DoubleConverter,
    org.infodavid.commons.converter.FloatConverter,
    org.infodavid.commons.converter.IntegerConverter,
    org.infodavid.commons.converter.LocalDateConverter,
    org.infodavid.commons.converter.LocalDateTimeConverter,
    org.infodavid.commons.converter.LongConverter,
    org.infodavid.commons.converter.ShortConverter,
    org.infodavid.commons.converter.SqlDateConverter,
    org.infodavid.commons.converter.SqlTimeConverter,
    org.infodavid.commons.converter.SqlTimestampConverter,
    org.infodavid.commons.converter.ZonedDateTimeConverter;

    uses org.apache.commons.beanutils.Converter;
    uses org.infodavid.commons.converter.ConverterWithDefaultType;

    requires transitive com.fasterxml.jackson.annotation;
    requires transitive commons.beanutils;
    requires transitive java.sql;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.commons.util;
    requires transitive org.slf4j;
}
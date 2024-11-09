module org.infodavid.commons.checksum {
    exports org.infodavid.commons.checksum;

    provides org.infodavid.commons.checksum.ChecksumGenerator with
    org.infodavid.commons.checksum.MD5ChecksumGenerator,
    org.infodavid.commons.checksum.SHA256ChecksumGenerator,
    org.infodavid.commons.checksum.SHA384ChecksumGenerator,
    org.infodavid.commons.checksum.SHA512ChecksumGenerator;

    uses org.infodavid.commons.checksum.ChecksumGenerator;

    requires transitive org.apache.commons.codec;
    requires transitive org.apache.commons.lang3;
    requires transitive org.infodavid.commons.util;
    requires transitive org.slf4j;
    requires lombok;
}
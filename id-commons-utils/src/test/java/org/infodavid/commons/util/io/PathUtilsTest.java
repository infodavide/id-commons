package org.infodavid.commons.util.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.infodavid.commons.test.TestCase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import net.lingala.zip4j.ZipFile;

/**
 * The Class PathUtilsTest.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class PathUtilsTest extends TestCase {

    /**
     * Test get group.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testGetGroup() throws Exception {
        final File file = new File("target/test-classes/pathutils_tests.png");

        final UserPrincipal group = PathUtils.getGroup(file.toPath());

        System.out.println(group);
        assertNotNull(group);
    }

    /**
     * Test get group with wrong path.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testGetGroupWithWrongPath() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.txt", "Null group");

        assertThrows(NoSuchFileException.class, () -> PathUtils.getGroup(file.toPath()), "Exception not raised or has a wrong type");
    }

    /**
     * Test get owner.
     * @throws Exception the exception
     */
    @Test
    void testGetOwner() throws Exception {
        final File file = new File("target/test-classes/pathutils_tests.png");

        final UserPrincipal owner = PathUtils.getOwner(file.toPath());

        System.out.println(owner);
        assertNotNull(owner);
    }

    /**
     * Test get owner with wrong path.
     * @throws Exception the exception
     */
    @Test
    void testGetOwnerWithWrongPath() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.txt", "Null owner");

        assertThrows(NoSuchFileException.class, () -> PathUtils.getOwner(file.toPath()), "Exception not raised or has a wrong type");
    }

    /**
     * Test get permissions.
     * @throws Exception the exception
     */
    @Test
    void testGetPermissions() throws Exception {
        final File file = new File("target/test-classes/pathutils_tests.png");

        final Set<AclEntry> permissions = PathUtils.getPermissions(file.toPath());

        System.out.println(permissions);
        assertNotNull(permissions, "Null permissions");
        assertFalse(permissions.isEmpty());
    }

    /**
     * Test get permissions with wrong path.
     * @throws Exception the exception
     */
    @Test
    void testGetPermissionsWithWrongPath() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.txt", "Empty permissions");

        assertThrows(NoSuchFileException.class, () -> PathUtils.getPermissions(file.toPath()), "Exception not raised or has a wrong type");
    }

    /**
     * Test set owner.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testSetOwner() throws Exception {
        final File file = new File("target/test-classes/pathutils_tests.png");
        final UserPrincipal owner = PathUtils.getOwner(file.toPath());
        final GroupPrincipal group = PathUtils.getGroup(file.toPath());
        System.out.println(owner);

        PathUtils.setOwner(file.toPath(), owner.getName(), group == null ? null : group.getName());

        assertEquals(owner, PathUtils.getOwner(file.toPath()), "Wrong owner");
    }

    /**
     * Test set owner with wrong path.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testSetOwnerWithWrongPath() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.txt");

        assertThrows(NoSuchFileException.class, () -> PathUtils.setOwner(file.toPath(), null, null), "Exception not raised or has a wrong type");
    }

    /**
     * Test set permissions.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testSetPermissions() throws Exception {
        final File file = new File("target/test-classes/pathutils_tests.png");
        final UserPrincipal owner = PathUtils.getOwner(file.toPath());
        Set<AclEntry> permissions = new HashSet<>();
        permissions.add(AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(owner).setPermissions(AclEntryPermission.READ_DATA).build());
        permissions.add(AclEntry.newBuilder().setType(AclEntryType.DENY).setPrincipal(owner).setPermissions(AclEntryPermission.WRITE_DATA).build());

        PathUtils.setPermissions(file.toPath(), permissions);

        permissions = PathUtils.getPermissions(file.toPath());
        System.out.println(permissions);
        assertNotNull(permissions, "Null permissions");
        assertFalse(permissions.isEmpty(), "Empty permissions");
        assertTrue(Files.isReadable(file.toPath()), "Wrong permission");
        assertFalse(Files.isWritable(file.toPath()));
    }

    /**
     * Test set permissions with wrong path.
     * @throws Exception the exception
     */
    @EnabledOnOs(OS.LINUX)
    @Test
    void testSetPermissionsWithWrongPath() throws Exception {
        final File file = new File("target/test-classes/checksum_tests.txt", "Wrong permission");

        assertThrows(NoSuchFileException.class, () -> PathUtils.setPermissions(file.toPath(), new HashSet<>()), "Exception not raised or has a wrong type");
    }

    /**
     * Test unzip.
     * @throws Exception the exception
     */
    @Test
    void testUnzip() throws Exception {
        final File file = new File("target/" + getClass().getSimpleName() + ".zip");
        final File extractedDir = new File("target/extracted_" + getClass().getSimpleName());
        final File sourceDir = new File("src/test/resources");

        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        if (extractedDir.exists()) {
            FileUtils.deleteQuietly(extractedDir);
        }

        try (final ZipFile zip = new ZipFile(file)) {

            for (final File f : sourceDir.listFiles()) {
                if (f.isDirectory()) {
                    zip.addFolder(f);
                } else if (f.isFile() && !f.isHidden() && !f.getName().startsWith(".")) {
                    zip.addFile(f);
                }
            }
        }

        try (InputStream in = new FileInputStream(file)) {
            CompressionUtils.unzip(in, extractedDir.toPath(), Collections.emptySet());
        }

        assertTrue(extractedDir.exists(), "Directory not created");

        try {
            System.out.println(PathUtils.printDirectoryTree(sourceDir.toPath()));
            System.out.println(PathUtils.printDirectoryTree(extractedDir.toPath(), true));
            assertTrue(PathUtils.compare(sourceDir.toPath(), extractedDir.toPath()), "Directory content is wrong");
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * Test unzip with invalid file.
     * @throws Exception the exception
     */
    @Test
    void testUnzipWithInvalidFile() throws Exception {
        final File file = new File("target/" + getClass().getSimpleName() + ".zip");
        final File extractedDir = new File("target/extracted_" + getClass().getSimpleName());

        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        FileUtils.copyFile(new File("target/test-classes/pathutils_tests.png"), file);

        assertThrows(IOException.class, () -> { // NOSONAR No lambda
            try (InputStream in = new FileInputStream(file)) {
                CompressionUtils.unzip(in, extractedDir.toPath(), Collections.emptySet());
            } finally {
                FileUtils.deleteQuietly(file);
            }
        }, "Exception not raised or has a wrong type");
    }

    /**
     * Test zip.
     * @throws Exception the exception
     */
    @Test
    void testZip() throws Exception {
        final File file = new File("target/" + getClass().getSimpleName() + ".zip");
        final File extractedDir = new File("target/extracted_" + getClass().getSimpleName());
        final File sourceDir = new File("src/test/resources");

        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        if (extractedDir.exists()) {
            FileUtils.deleteQuietly(extractedDir);
        }

        try (OutputStream out = new FileOutputStream(file)) {
            CompressionUtils.zip(sourceDir.toPath(), out, Collections.emptySet(), true);
        }

        assertTrue(file.exists(), "File not created");

        try {
            assertTrue(file.length() > 0, "Created file is empty");

            try (final ZipFile zip = new ZipFile(file)) {
                zip.extractAll(extractedDir.getAbsolutePath());
            }

            System.out.println(PathUtils.printDirectoryTree(sourceDir.toPath()));
            System.out.println(PathUtils.printDirectoryTree(extractedDir.toPath(), true));
            assertTrue(PathUtils.compare(sourceDir.toPath(), extractedDir.toPath()), "Zip is malformed");
        } finally {
            FileUtils.deleteQuietly(file);
            FileUtils.deleteQuietly(extractedDir);
        }
    }

    /**
     * Test zip with invalid path.
     * @throws Exception the exception
     */
    @Test
    void testZipWithInvalidPath() throws Exception {
        final File file = new File("target/p1.zip");

        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }

        assertThrows(NoSuchFileException.class, () -> { // NOSONAR No lambda
            try (OutputStream out = new FileOutputStream(file)) {
                CompressionUtils.zip(new File("invalid").toPath(), out, Collections.emptySet(), false);
            }
        }, "Exception not raised or has a wrong type");
        assertTrue(file.exists(), "File not created");
        FileUtils.deleteQuietly(file);
    }
}

package relevant_craft.vento.r_launcher.manager.extract;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.tukaani.xz.XZInputStream;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ExtractManager {

    public static void unpackZIP(String zipFile, String extractFolder) throws IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = extractFolder;

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();

            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
        }
        zip.close();
    }

    public static void packZIP(String sourceDirectory, String zipFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(zipFile)));
        Files.walkFileTree(Paths.get(sourceDirectory), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    zos.putNextEntry(new ZipEntry(Paths.get(sourceDirectory).relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                } catch (Exception ignored) {
                    return null;
                }
            }
        });
        zos.close();
    }

    public static void unpackLZMA(String pack, String jar) throws IOException {
        // Create the Packer object
        Pack200.Packer packer = Pack200.newPacker();

        // Initialize the state by setting the desired properties
        Map p = packer.properties();
        // take more time choosing codings for better compression
        p.put(Pack200.Packer.EFFORT, "7");  // default is "5"
        // use largest-possible archive segments (>10% better compression).
        p.put(Pack200.Packer.SEGMENT_LIMIT, "-1");
        // reorder files for better compression.
        p.put(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.FALSE);
        // smear modification times to a single value.
        p.put(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.LATEST);
        // ignore all JAR deflation requests,
        // transmitting a single request to use "store" mode.
        p.put(Pack200.Packer.DEFLATE_HINT, Pack200.Packer.FALSE);
        // discard debug attributes
        p.put(Pack200.Packer.CODE_ATTRIBUTE_PFX + "LineNumberTable", Pack200.Packer.STRIP);
        // throw an error if an attribute is unrecognized
        p.put(Pack200.Packer.UNKNOWN_ATTRIBUTE, Pack200.Packer.ERROR);
        // pass one class file uncompressed:
        p.put(Pack200.Packer.PASS_FILE_PFX + 0, "mutants/Rogue.class");
        File f = new File(pack);
        FileOutputStream fostream = new FileOutputStream(jar);
        JarOutputStream jostream = new JarOutputStream(fostream);
        Pack200.Unpacker unpacker = Pack200.newUnpacker();
        // Call the unpacker
        unpacker.unpack(f, jostream);
        // Must explicitly close the output.
        jostream.close();
    }

    public static void unpackXZ(String xz, String pack) throws IOException {
        FileInputStream fin = new FileInputStream(xz);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream out = new FileOutputStream(pack);
        XZInputStream xzIn = new XZInputStream(in);
        final byte[] buffer = new byte[8192];
        int n = 0;
        while (-1 != (n = xzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        xzIn.close();
    }

    public static void extractTarGZ(File archive, File file) throws Exception {
        FileInputStream fileInput = new FileInputStream(archive);
        GzipCompressorInputStream gzipInput = new GzipCompressorInputStream(fileInput);
        TarArchiveInputStream tarInput = new TarArchiveInputStream(gzipInput);
        FileOutputStream fileOutput = new FileOutputStream(file);

        while (tarInput.getNextTarEntry() != null) {
            int length;
            byte[] data = new byte[1024];

            while ((length = tarInput.read(data, 0, data.length)) != -1) {
                fileOutput.write(data, 0, length);
            }
        }

        fileOutput.close();
        tarInput.close();
        gzipInput.close();
        fileInput.close();
    }

}

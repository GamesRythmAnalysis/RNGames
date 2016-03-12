package fr.utbm.rngames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    public static void createZipArchive(URL destination, List<URL> files) throws IOException {
        byte[] buffer = new byte[1024];
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(destination.getPath()));

        for (URL fileURL : files) {
            File entry = new File(fileURL.getPath());

            FileInputStream input = new FileInputStream(entry);

            zipFile.putNextEntry(new ZipEntry(entry.getName()));

            int length;

            while ((length = input.read(buffer)) > 0) {
                zipFile.write(buffer, 0, length);
            }

            zipFile.closeEntry();
            input.close();
        }

        zipFile.close();
    }
}

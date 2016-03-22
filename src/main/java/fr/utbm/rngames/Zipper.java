package fr.utbm.rngames;

import org.arakhne.afc.vmutil.locale.Locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper implements AutoCloseable {
	public static final String EXTENSION_NAME = Locale.getString("save.extension.name");

    private final byte[] buffer = new byte[1024];
    private final ZipOutputStream zipFile;

    public Zipper(URL destination) throws FileNotFoundException {
        this.zipFile = new ZipOutputStream(new FileOutputStream(destination.getPath()));
    }

    public void addFile(URL file, String newNameInZip) throws IOException {
        File entry = new File(file.getPath());

        try (FileInputStream input = new FileInputStream(entry)) {
            this.zipFile.putNextEntry(new ZipEntry(newNameInZip));

            int length;
            while ((length = input.read(this.buffer)) > 0) {
                this.zipFile.write(this.buffer, 0, length);
            }

            this.zipFile.closeEntry();
        }
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
}

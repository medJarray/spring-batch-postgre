package com.medjar.batch.productimport.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created By Jarray Mohamed.
 * E-mail : jarraymohamed92@hotmail.fr
 *
 * @Date mai 09, 2022.
 */
@UtilityClass
public class FileUtils {

    public static List<File> extractTargzFile(InputStream gzTarFile) throws IOException {
        List<File> files = new ArrayList<>();
        try (GzipCompressorInputStream gcis = new GzipCompressorInputStream(gzTarFile);
             TarArchiveInputStream tis = new TarArchiveInputStream(gcis)) {
            ArchiveEntry tarEntry = tis.getNextTarEntry();

            while (Objects.nonNull(tarEntry)) {
                String fileName = tarEntry.getName();
                File newFile = File.createTempFile(fileName, null);
                newFile.deleteOnExit();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    IOUtils.copy(tis, fos);
                }
                files.add(newFile);
                tarEntry = tis.getNextTarEntry();
            }
        }
        return files;
    }
}

package ru.ilya.parsers.file;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserFilesScanner {
    public boolean isEligibleForScholarship(Path p, String excludeFilename) {
        return !Files.isDirectory(p) &&
                !p.getFileName().toString().equals(excludeFilename);
    }

    public List<String> scan(Path path, String excludeFilename) {
        List<String> result;
        try (Stream<Path> paths = Files.walk(path)) {
            result = paths
                    .filter(p -> this.isEligibleForScholarship(p, excludeFilename))
                    .map(p -> new File(p.toString()))
                    .sorted(Comparator.comparing(File::getName))
                    .map(File::getPath)
                    .filter(name -> name.endsWith("txt"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public void openFile(String filename) {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(FileSystems.getDefault().getPath(filename).toAbsolutePath().toString());
            desktop.open(dirToOpen);
        } catch (Exception e) {
            throw new RuntimeException(String.format("File %s doesn't exist", filename));
        }
    }
}

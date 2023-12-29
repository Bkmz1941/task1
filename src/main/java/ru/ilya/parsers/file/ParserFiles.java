package ru.ilya.parsers.file;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserFiles {
    private List<String> sortedFiles;
    private final List<String> validOrderFiles;
    private final Stack<String> stackFiles;
    private final String resultFileName;
    private final ParserFilesScanner scannerDelegate;
    private final ParserFilesPrinter printerDelegate;

    public ParserFiles(String resultFileName) {
        this.sortedFiles = new ArrayList<>();
        this.validOrderFiles = new ArrayList<>();
        this.stackFiles = new Stack<>();
        this.resultFileName = resultFileName + ".txt";
        this.scannerDelegate = new ParserFilesScanner();
        this.printerDelegate = new ParserFilesPrinter();
    }

    public void run() {
        this.scanTextFiles().solveSortedFiles().combineFilesContent().printValidOrderFiles().openResultFile().clear();
    }

    public ParserFiles openResultFile() {
        if (!this.validOrderFiles.isEmpty()) {
            this.scannerDelegate.openFile(this.resultFileName);
        }
        return this;
    }

    public ParserFiles scanTextFiles() {
        this.sortedFiles = this.scannerDelegate.scan(Paths.get(System.getProperty("user.dir")), this.resultFileName);
        return this;
    }

    public ParserFiles solveSortedFiles() {
        if (!this.sortedFiles.isEmpty()) {
            for (String file : this.sortedFiles) {
                this.solveFileOrDependency(file);
            }
        } else {
            System.out.println("No files");
        }

        return this;
    }

    private void solveFileOrDependency(String file) {
        try {
            if (this.stackFiles.contains(file)) {
                throw new RuntimeException("Error: cyclic dependency between files: " + this.stackFiles);
            }

            if (!validOrderFiles.contains(file)) {
                validOrderFiles.add(file);
            }

            this.stackFiles.add(file);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("require")) {
                    Optional<String> optionalFilename = this.getDependencyFilename(line);

                    if (optionalFilename.isPresent()) {
                        Path dependencyPath = FileSystems.getDefault().getPath(optionalFilename.get()).toAbsolutePath();

                        if (!Files.exists(dependencyPath)) {
                            throw new Exception(String.format("File %s doesn't exist", dependencyPath));
                        }
                        this.solveFileOrDependency(dependencyPath.toString());

                        int currFileIdx = this.validOrderFiles.indexOf(file);
                        int dependencyFileIdx = this.validOrderFiles.indexOf(dependencyPath.toString());

                        if (currFileIdx < dependencyFileIdx) {
                            Collections.swap(this.validOrderFiles, currFileIdx, dependencyFileIdx);
                        }
                    }
                }
                line = reader.readLine();
            }

            this.stackFiles.pop();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ParserFiles combineFilesContent() {
        if (this.validOrderFiles.isEmpty()) {
            return this;
        }

        File resultFile = new File(this.resultFileName);

        try {
            BufferedWriter writer = null;
            BufferedReader reader = null;

            writer = new BufferedWriter(new FileWriter(resultFile));
            for (String file : this.validOrderFiles) {;
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    writer.write(line);
                    writer.newLine();
                    line = reader.readLine();
                }
                reader.close();
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this;
    }

    private Optional<String> getDependencyFilename(String input) {
        Matcher matcherPath = Pattern.compile("'(.*.txt)'").matcher(input);
        if (matcherPath.find()) return Optional.of(matcherPath.group(1));
        return Optional.empty();
    }

    public ParserFiles printValidOrderFiles() {
        this.printerDelegate.printFiles(this.validOrderFiles);
        return this;
    }

    public void clear() {
        this.sortedFiles.clear();
        this.validOrderFiles.clear();
    }
}

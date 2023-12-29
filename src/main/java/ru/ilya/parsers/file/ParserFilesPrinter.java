package ru.ilya.parsers.file;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParserFilesPrinter {
    public void printFiles(List<String> files) {
        if (!files.isEmpty()) {
            System.out.println("Finally order of files:");
            int idx = 1;
            for (String file : files) {
                System.out.printf("%s: %s\n", idx++, file);
            }
        }
    }
}

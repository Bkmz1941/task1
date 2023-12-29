package ru.ilya;

import ru.ilya.parsers.file.ParserFiles;

public class Main {
    public static void main(String[] args) {
        try {
            ParserFiles fileComposer = new ParserFiles("result");
            fileComposer.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
package com.sharvari.expensemanager.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtil {

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }
        return lines;
    }


    public static void writeLines(String filePath, List<String> lines) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + filePath);
        }
    }


    public static void appendLine(String filePath, String line) {
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath);
        }
    }

    public static void ensureFileExists(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fatal error creating file: " + filePath, e);
        }
    }


}

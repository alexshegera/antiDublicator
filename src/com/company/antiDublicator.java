package com.company;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class antiDublicator {

   private static List<MapOfFiles> mapOfFiles = new ArrayList<>();
   private static String rootPath = ".";
   private static String mask = "*.bak";
   private static Boolean doDelete = false;

    public static void main(String[] args) throws IOException {

        if (args.length > 0) {
            for (String arg:args) {
                if (arg.toLowerCase().contains("path=")) {
                    rootPath = arg.substring(arg.indexOf("=") + 1);
                }
                if (arg.toLowerCase().contains("mask=")) {
                    mask = arg.substring(arg.indexOf("=") + 1);
                }
                if (arg.equalsIgnoreCase("-delete") || arg.equalsIgnoreCase("-d")) {
                    doDelete = true;
                }
            }
        } else {
            System.out.println("ex., antuDublicator -path=D:\\temp\\ -mask=*.jp* -delete");
        }

        System.out.print("Searching files on the rootPath = " + rootPath + " .... ");
        searchFilesAndCalculateMD5Sum(rootPath, "glob:" + mask);
        System.out.println("Working ...");

        if (mapOfFiles.stream().count() > 1) {
            for (MapOfFiles file1:mapOfFiles) {
                for (MapOfFiles file2:mapOfFiles) {
                    if ((!file1.getPath().equals(file2.getPath())) && (file1.getMd5Sum().equals(file2.getMd5Sum())) && !file2.getProcessing()) {
                        System.out.println("Found equals - " + file1.getPath() + " and " + file2.getPath());
                        if (doDelete) {
                            System.out.println("File " + file2.getPath() + " deleted!");
                            File fileToDelete = new File(file2.getPath());
                            fileToDelete.delete();
                        } else {
                            System.out.println("skip delete!");
                        }
                    }
                }
                file1.setProcessing(true);
            }
        }
        System.out.println("Finish! :)");
    }

    public static void searchFilesAndCalculateMD5Sum(String rootPath, String pattern) {
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                FileSystem fs = FileSystems.getDefault();
                PathMatcher matcher = fs.getPathMatcher(pattern);
                Path name = file.getFileName();
                Path absolutePathToFile = file.toAbsolutePath();
                MapOfFiles currentFile = new MapOfFiles();
                currentFile.setPath(absolutePathToFile.toString());
                try {
                    currentFile.setMd5Sum(getMD5Sum(absolutePathToFile.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (matcher.matches(name)) {
                    mapOfFiles.add(currentFile);
                }
                return FileVisitResult.CONTINUE;
            }
        };
        Path rootDir = Paths.get(rootPath);
        try {
            Files.walkFileTree(rootDir, matcherVisitor);
            System.out.println("done");
        } catch (IOException e) {
            System.out.println("no files found, try again!");
        }
    }

    public static String getMD5Sum(String fileName) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1)
        {
            digest.update(byteArray, 0, bytesCount);
        };
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sumMD5 = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sumMD5.append(Integer
                    .toString((bytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sumMD5.toString();
    }
}

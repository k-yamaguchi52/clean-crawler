package com.example.cleancrawler.crawler.util;

import java.io.File;

public class FileUtil {

    public static void createRecursiveDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            createRecursiveDirs(file.getParent());
            createDir(path);
        }
    }

    private static void createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}

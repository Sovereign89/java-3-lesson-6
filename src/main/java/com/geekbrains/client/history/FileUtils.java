package com.geekbrains.client.history;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileUtils {

    private final String datePattern = "yyyy-MM-dd";
    private final DateFormat dateFormat = new SimpleDateFormat(datePattern);
    private final Object lock = new Object();
    private final String fileFormat = ".txt";
    private final String historyFolder;

    public FileUtils() {
        String historyFolderTemp;
        try {
            // Пытаемся достать путь до рабочей папки с приложением
            historyFolderTemp = new File(".").getCanonicalPath();
        } catch (IOException e) {
            // Если не удалось, пытаемся снова но по другому
            historyFolderTemp = System.getProperty("user.dir");
        }
        this.historyFolder = historyFolderTemp + "/history/";
    }

    private File getLastHistoryFile(String folder){
        File dir = new File(folder);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastHistoryFile = files[0];
        for (int i = 1; i < files.length; i++) {
            // Достаём наиболее раннюю историю по клиенту
            if (lastHistoryFile.lastModified() < files[i].lastModified()) {
                lastHistoryFile = files[i];
            }
        }
        return lastHistoryFile;
    }

    public boolean writeToFile(String text, String handler) {
        Date today = Calendar.getInstance().getTime();
        String todayAsString = dateFormat.format(today);

        String handlerFolder = historyFolder+handler+"/";

        String file = handlerFolder+todayAsString+fileFormat;

        File directory = new File(handlerFolder);
        if (!directory.exists()){
            Boolean isCreated =directory.mkdir();
            System.out.println("Folder is created: " + isCreated);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            synchronized (lock) {
                writer.write(text);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<String> readFromFile(String handler) {
        String handlerFolder = historyFolder+handler+"/";
        ArrayList<String> stringList = new ArrayList<>();
        // Ищем последний файл истории
        File lastHistoryFile = getLastHistoryFile(handlerFolder);
        if (lastHistoryFile == null) {
            return stringList;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(lastHistoryFile))) {
            synchronized (lock) {
                for (String line; (line = reader.readLine()) != null;) {
                    stringList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return stringList;
        }
        stringList.trimToSize();
        return stringList;
    }
}

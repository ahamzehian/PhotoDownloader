package com.amirportfolio;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String urlPath = "https://website.com/";
    private static final String searchCommand = "index.php?page=post&s=list&tags=";
    private static String searchSubject;
    private static final String pid = "&pid=";

    private static final int pidIndex = 42;

    private static final List<String> indexes = new ArrayList<>();
    private static final List<String> urls = new ArrayList<>();
    private static final List<String> srcs = new ArrayList<>();

    private static String saveLocation;

    private static File savedFile;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("What subject do you want to search?");
        searchSubject = scanner.nextLine();

        System.out.println("for how many pages? ");
        int numberOfPages = scanner.nextInt();

        setSaveLocation();

        createSavingFolder();

        for (int i = 0; i < numberOfPages; i++) {

            hrefIterator(i);

            generateUrls();

            listSources();

            savePhotos(i);

            resetAllLists();

        }

        System.out.println("finished");

    }

    /**
     * gather all value of href in a page and list them in hrefs list while printing each value
     */
    private static void hrefIterator(int numberOfPages) {
        try {

            int currentPageNumber = pidIndex * (numberOfPages);

            URL finalUrl = new URL(urlPath + searchCommand + searchSubject + pid + currentPageNumber);

            BufferedReader in = new BufferedReader(new InputStreamReader(finalUrl.openStream()));

            String result;
            int count = 1;

            while ((result=in.readLine()) != null) {
                if (result.contains("href")) {
                    System.out.print(count + ".\t");
                    printAndAddHref(result);
                }
                count++;
            }

            in.close();

        } catch (MalformedURLException e) {
            System.out.println("Failed to get the url! " + e.getCause());
            System.out.println("Cause: " + e.getCause());
        } catch (IOException e) {
            System.out.println("Failed to run bufferedReader! " + e.getMessage());
        }
    }


    /**
     * Finds all lines of htm with href and print their value
     * It also adds all href values that contains "index" in a list called indexes
     * Indexes only works for some websites.
     * Indexes are the address of each thumbnail.
     */
    private static void printAndAddHref(String lineOfCode) {
        int hrefIndex = lineOfCode.indexOf("href");
        String subject = lineOfCode.substring(hrefIndex + ("href").length());
        int begin,end;

        begin = subject.indexOf("\"") + 1;
        end = subject.substring(begin).indexOf("\"") + 2;

        String result = subject.substring(begin,end);

        System.out.println(result);

        if (result.contains("index") && !result.contains("https")) {
            indexes.add(result);
        }
    }


    /**
     * By using indexes, the method generate all urls with website name and its index
     */
    private static void generateUrls() {
        String website = "https://rule34.xxx/";
        for (String index:indexes) {
            String url = website + index;
            System.out.println(url);
            if (url.contains("view&id")) {
                urls.add(url);
            }
        }
    }


    private static void printAndAddSRC(String lineOfCode) {
        int hrefIndex = lineOfCode.indexOf("src");
        String subject = lineOfCode.substring(hrefIndex + ("src").length());
        int begin,end;

        begin = subject.indexOf("\"") + 1;
        end = subject.substring(begin).indexOf("\"") + 2;

        String result = subject.substring(begin,end);
        srcs.add(result);

        System.out.println(result);

    }


    /**
     * Print and gather all SRS values in srcs list
     */
    private static void listSources() {
        try {

            for (String url:urls) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));

                String line;

                while ((line = in.readLine()) != null) {
                    if (line.contains("src") && line.contains("img") && line.contains("id")) {
                        System.out.print("SRC: ");
                        printAndAddSRC(line);
                    }
                }
            }

        }catch (MalformedURLException e) {
            System.out.println("Failed to use url to get SRC! " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        } catch (IOException e) {
            System.out.println("Failed to read src! " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
    }


    private static void setSaveLocation() {
        saveLocation = "C:\\Users\\amirh\\Desktop\\SQL\\scrap test\\" + searchSubject;
    }


    private static void createSavingFolder() {

        savedFile = new File(saveLocation);
        if (!savedFile.exists()) {
            savedFile.mkdirs();
            System.out.println("Folder successfully created.");
        }

    }


    /**
     * Attention: this method is only saving jpg files.
     */
    private static void savePhotos(int numberOfPages) {

        try {

            int count = 0;

            for (String src:srcs) {
                URL url = new URL(src);
                if (src.contains("jpg") || src.contains("jpeg")) {
                    BufferedImage image = ImageIO.read(url);
                    if (image.getHeight() > 150) {
                        String photoName = searchSubject + numberOfPages + count;
                        ImageIO.write(image, "jpg", new File(saveLocation + "\\" + photoName + ".jpg"));
                        count++;
                        System.out.println(photoName + " saved");
                    }
                }
            }

        } catch (MalformedURLException e) {
            System.out.println("Failed to use url! " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        } catch (IOException e) {
            System.out.println("Failed to save image! " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }

    }


    private static void resetAllLists() {
        indexes.clear();
        urls.clear();
        srcs.clear();
    }

}

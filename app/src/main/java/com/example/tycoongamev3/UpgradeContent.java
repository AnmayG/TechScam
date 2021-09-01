package com.example.tycoongamev3;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeContent {

    /**
     * An array of sample (Upgrade) items.
     */
    public static final List<UpgradeItem> ITEMS = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();
    private static final ArrayList<String> upgradeNames = new ArrayList<>();

    /**
     * A map of sample (Upgrade) items, by ID.
     * Came with standard ListView template.
     */
    public static final Map<String, UpgradeItem> ITEM_MAP = new HashMap<String, UpgradeItem>();

    // This is a static version of the constructor that gets run when the class is loaded.
    // It came with the template.
    static {
        readFile("upgrade_names.txt");
        for (int i = 0; i < upgradeNames.size(); i++) {
            addItem(createUpgradeItem(i));
        }
    }

    /**
     * Reads the file "upgrade_names.txt" and updates upgradeNames using those values
     * Source: https://stackoverflow.com/questions/9544737/read-file-from-assets
     */
    public static void readFile(String fileName){
        BufferedReader reader = null;
        try {
            Context mActivityContext = MainActivity.getmContext().getApplicationContext();
            reader = new BufferedReader(
                    new InputStreamReader(mActivityContext.getAssets().open(fileName)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                upgradeNames.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addItem(UpgradeItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static UpgradeItem createUpgradeItem(int position) {
        return new UpgradeItem(String.valueOf(position), upgradeNames.get(position), makeDetails(position));
    }

    private static String makeDetails(int position) {
        return "Upgrades\n" + businesses.get(position % businesses.size()).getName();
    }

    /**
     * A Upgrade item representing a piece of content.
     */
    public static class UpgradeItem {
        public final String id;
        public final String content;
        public final String details;

        public UpgradeItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
package com.example.tycoongamev3;

import com.example.tycoongamev3.Business;
import com.example.tycoongamev3.MainActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerContent {

    /**
     * An array of sample (Manager) items.
     */
    public static final List<ManagerItem> ITEMS = new ArrayList<>();
    private static final ArrayList<Business> businesses = MainActivity.getBusinesses();
    private static final String[] managerNames = {
            "Bobby Burglar",
            "Niger Prince",
            "TokTik",
            "Spamuel Spam",
            "Sequel Person",
            "Harvard Grad",
            "Mr. Red Button"
    };

    /**
     * A map of sample (Manager) items, by ID.
     */
    public static final Map<String, ManagerItem> ITEM_MAP = new HashMap<String, ManagerItem>();

    private static final int COUNT = businesses.size();

    // This came with the template and is kind of like a constructor that is called when the class is created
    static {
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createManagerItem(i));
        }

    }

    private static void addItem(ManagerItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ManagerItem createManagerItem(int position) {
        return new ManagerItem(String.valueOf(position), managerNames[position], makeDetails(position),
                BigDecimal.valueOf(10).pow(position + 3).multiply(BigDecimal.valueOf(businesses.get(position).getInitCost())));
    }

    private static String makeDetails(int position) {
        return "Manages " + businesses.get(position).getName();
    }

    /**
     * A Manager item representing a piece of content.
     */
    public static class ManagerItem {
        public final String id;
        public final String content;
        public final String details;
        public final BigDecimal price;

        public ManagerItem(String id, String content, String details, BigDecimal price) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.price = price;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
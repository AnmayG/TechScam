package com.example.tycoongamev3;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

// Converts a number to a name
// https://stackoverflow.com/questions/24434555/determine-million-billion-trillion-quadrillion-in-java
public class NumberNames {
    static void test(String numberString, String string) {
        BigDecimal number = new BigDecimal(numberString);
        System.out.println(number+" is "+createString(number)+" should be "+string);
    }

    private static final String NAMES[] = new String[] {
            "K",
            "M",
            "B",
            "t",
            "q",
            "Q",
            "s",
            "S",
            "o",
            "n",
            "d",
            "U",
            "D",
            "T",
            "Qt",
            "Qd",
            "Sd",
            "St",
            "O",
            "N",
            "v",
    };
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final NavigableMap<BigDecimal, String> MAP;
    static {
        MAP = new TreeMap<>();
        for (int i=0; i<NAMES.length; i++) {
            MAP.put(THOUSAND.pow(i+1), NAMES[i]);
        }
    }

    public static String createString(BigDecimal number) {
        Entry<BigDecimal, String> entry = MAP.floorEntry(number);
        if (entry == null)
        {
            return number.toString();
        }
        BigDecimal key = entry.getKey();
        BigDecimal d = key.divide(THOUSAND, RoundingMode.HALF_DOWN);
        BigDecimal m = number.divide(d, RoundingMode.HALF_DOWN);
        float f = m.floatValue() / 1000.0f;
        float rounded = ((int) (f * 100.0)) / 100.0f;
        if (rounded % 1 == 0)
        {
            return ((int) rounded) + entry.getValue();
        }
        return rounded + entry.getValue();
    }
}
package name.ulbricht.javafx.gallery.pages.data;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Simple sample data (word lists and generated item lists) for the demo pages.
 */
public final class SampleData {

    /** A short list of fruit names. */
    public static final List<String> FRUITS = List.of(
            "Apple", "Apricot", "Banana", "Blackberry", "Blueberry", "Cherry", "Coconut", "Cranberry",
            "Grape", "Grapefruit", "Kiwi", "Lemon", "Lime", "Mango", "Melon", "Orange", "Papaya", "Peach",
            "Pear", "Pineapple", "Plum", "Raspberry", "Strawberry", "Watermelon");

    /** A list of city names. */
    public static final List<String> CITIES = List.of(
            "Amsterdam", "Berlin", "Copenhagen", "Dublin", "Edinburgh", "Florence", "Geneva", "Hamburg",
            "Istanbul", "Krakow", "Lisbon", "Madrid", "Oslo", "Paris", "Prague", "Rome", "Stockholm", "Vienna");

    private SampleData() {
    }

    /**
     * Generates numbered items such as {@code "Item 0001"}.
     *
     * @param prefix text before the number
     * @param count  number of items
     * @return an immutable list
     */
    public static List<String> generateItems(String prefix, int count) {
        int digits = String.valueOf(count).length();
        String format = "%s %0" + digits + "d";
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> String.format(format, prefix, i))
                .toList();
    }
}

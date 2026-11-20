package name.ulbricht.javafx.gallery.pages.data;

import javafx.scene.control.TreeItem;

import java.time.LocalDate;
import java.util.Locale;

/**
 * An entry of a fake file system, used by the tree demos.
 *
 * @param name      file or folder name
 * @param directory {@code true} for folders
 * @param size      size in bytes (for folders the sum of all contained files)
 * @param modified  last modification date
 */
public record FileItem(String name, boolean directory, long size, LocalDate modified) {

    /** A human-readable type description derived from the file extension. */
    public String type() {
        if (directory) {
            return "Folder";
        }
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "File" : name.substring(dot + 1).toUpperCase(Locale.ROOT) + " file";
    }

    /** The size formatted as B, KB, MB or GB. */
    public String formattedSize() {
        return formatSize(size);
    }

    /** Formats a byte count as B, KB, MB or GB. */
    public static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        String[] units = {"KB", "MB", "GB", "TB"};
        double value = bytes;
        int unit = -1;
        while (value >= 1024 && unit < units.length - 1) {
            value /= 1024;
            unit++;
        }
        return String.format(Locale.ROOT, "%.1f %s", value, units[unit]);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Builds a small sample file system tree. Folder sizes are the sums of their contents.
     *
     * @return the expanded root item
     */
    public static TreeItem<FileItem> createSampleTree() {
        LocalDate d = LocalDate.of(2025, 9, 15);
        TreeItem<FileItem> root = dir("This PC", d,
                dir("Documents", d.minusDays(3),
                        dir("Projects", d.minusDays(5),
                                dir("javafx-gallery", d.minusDays(1),
                                        file("pom.xml", 4_812, d.minusDays(2)),
                                        file("README.md", 2_310, d.minusDays(1)),
                                        dir("src", d.minusDays(1),
                                                file("Main.java", 1_204, d.minusDays(4)),
                                                file("Gallery.java", 8_731, d.minusDays(1)),
                                                file("gallery.css", 6_402, d.minusDays(2)))),
                                dir("website", d.minusDays(40),
                                        file("index.html", 12_400, d.minusDays(40)),
                                        file("style.css", 3_921, d.minusDays(41)))),
                        file("Budget 2025.xlsx", 48_221, d.minusDays(30)),
                        file("Letter.docx", 23_002, d.minusDays(12)),
                        file("Notes.txt", 812, d.minusDays(2))),
                dir("Pictures", d.minusDays(7),
                        dir("Holiday", d.minusDays(60),
                                file("Beach.jpg", 3_402_112, d.minusDays(61)),
                                file("Mountains.jpg", 4_120_331, d.minusDays(60)),
                                file("Sunset.png", 7_903_224, d.minusDays(60))),
                        file("Avatar.png", 92_310, d.minusDays(7)),
                        file("Wallpaper.jpg", 2_811_004, d.minusDays(90))),
                dir("Music", d.minusDays(100),
                        file("Theme.mp3", 5_340_221, d.minusDays(100)),
                        file("Podcast episode 12.mp3", 48_002_112, d.minusDays(14))),
                dir("Downloads", d,
                        file("jdk-25_windows-x64.zip", 205_331_456, d),
                        file("javafx-sdk-25.zip", 61_240_118, d),
                        file("invoice-0815.pdf", 120_440, d.minusDays(9))));
        root.setExpanded(true);
        root.getChildren().getFirst().setExpanded(true);
        return root;
    }

    @SafeVarargs
    private static TreeItem<FileItem> dir(String name, LocalDate modified, TreeItem<FileItem>... children) {
        long size = 0;
        for (TreeItem<FileItem> child : children) {
            size += child.getValue().size();
        }
        TreeItem<FileItem> item = new TreeItem<>(new FileItem(name, true, size, modified));
        item.getChildren().setAll(java.util.Arrays.asList(children));
        return item;
    }

    private static TreeItem<FileItem> file(String name, long size, LocalDate modified) {
        return new TreeItem<>(new FileItem(name, false, size, modified));
    }
}

package sorting;

import java.io.*;
import java.util.*;

public class ArrayFactory {

    public enum Type { RANDOM, SORTED, INVERSELY_SORTED }

    private static final Random RNG = new Random();

    public static int[] generate(Type type, int size) {
        int[] arr = new int[size];
        switch (type) {
            case RANDOM:
                for (int i = 0; i < size; i++) arr[i] = RNG.nextInt(size * 10) + 1;
                break;
            case SORTED:
                for (int i = 0; i < size; i++) arr[i] = i + 1;
                break;
            case INVERSELY_SORTED:
                for (int i = 0; i < size; i++) arr[i] = size - i;
                break;
        }
        return arr;
    }

    public static int[] fromFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append(',');
        }
        String[] tokens = sb.toString().split("[,\\s]+");
        List<Integer> nums = new ArrayList<>();
        for (String tok : tokens) {
            tok = tok.trim();
            if (!tok.isEmpty()) {
                try { nums.add(Integer.parseInt(tok)); }
                catch (NumberFormatException ignored) {}
            }
        }
        return nums.stream().mapToInt(Integer::intValue).toArray();
    }

    public static String label(Type type) {
        switch (type) {
            case RANDOM:           return "Random";
            case SORTED:           return "Sorted";
            case INVERSELY_SORTED: return "Inversely Sorted";
            default:               return "Unknown";
        }
    }
}

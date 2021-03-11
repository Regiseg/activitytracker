package week07d04;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ShoppingList {

    public long calculateSum(String path) {
        long sum = 0;
        Path file = Path.of(path);
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                long amount = Long.parseLong(line.split(";")[1]);
                long price = Long.parseLong(line.split(";")[2]);
                sum += amount * price;
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Cannot load", ioe);
        }
        return sum;
    }


}

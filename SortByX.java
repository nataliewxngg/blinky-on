import java.util.*;

public class SortByX implements Comparator<Car> {
    // DESCRIPTION: a compare method utilized for the sort-by-x(pos) comparator
    // interface: sorts Car objects in ascending order of their x positions
    public int compare(Car c1, Car c2) { // PARAMETERS: 2 Car objects to compare
        return c1.getX() - c2.getX();
        // RETURNS:
        // -# if c1's x pos < c2's
        // 0 if c1's x pos == c2's
        // +# if c1's x pos > c2's
    }
}
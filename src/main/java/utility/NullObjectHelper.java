package utility;

import java.util.ArrayList;
import java.util.List;

public class NullObjectHelper {
    private NullObjectHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> ArrayList<T> getNotNullArrayList(List<T> list) {
        if (list != null) {
            return new ArrayList<>(list);
        } else {
            return new ArrayList<>();
        }
    }
}

package net.jtownson.annotation;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

class MissingValues {

    public static final int FIRST_ENTRY = 0;

    static List<Integer> missingValues(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return emptyList();
        }
        Collections.sort(values);
        List<Integer> missingValues = new ArrayList<>();
        int last = FIRST_ENTRY - 1;
        for (Integer value : values) {
            if (value - last > 1) {
                List<Integer> rangeToAdd = IntStream.range(last + 1, value).boxed().collect(toList());
                missingValues.addAll(rangeToAdd);
            }
            last = value;
        }
        return missingValues;
    }

    static Set<Integer> duplicateValues(Collection<Integer> values) {
        if (values == null || values.isEmpty()) {
            return emptySet();
        }
        return values.stream().filter(i -> Collections.frequency(values, i) > 1)
                .collect(toSet());
    }
}

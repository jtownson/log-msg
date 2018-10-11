package net.jtownson.annotation;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static net.jtownson.annotation.MissingValues.duplicateValues;
import static net.jtownson.annotation.MissingValues.missingValues;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MissingValuesTest {

    @Test
    public void missingValueOfEmpty() {
        assertThat(missingValues(emptyList()), is(emptyList()));
    }

    @Test
    public void contiguousNonZeroBasedShouldReturnMissingLeadEntries() {
        assertThat(missingValues(asList(1, 2, 3, 4)), is(asList(0)));
    }

    @Test
    public void contiguousZeroBasedListShouldReturnEmpty() {
        assertThat(missingValues(asList(0, 1, 2, 3, 4)), is(emptyList()));
    }

    @Test
    public void missing1ValMidRangeShouldBeLocated() {
        assertThat(missingValues(asList(0, 1, 3, 4)), is(asList(2)));
    }

    @Test
    public void missing2ValsMidRangeShouldBeLocated() {
        assertThat(missingValues(asList(0, 3)), is(asList(1, 2)));
    }

    @Test
    public void duplicatesOfEmpty() {
        assertThat(duplicateValues(emptyList()), is(emptySet()));
    }

    @Test
    public void duplicatesOfUnique() {
        assertThat(duplicateValues(asList(1, 2, 3, 4)), is(emptySet()));
    }

    @Test
    public void duplicatesOfNonUnique() {
        assertThat(duplicateValues(asList(1, 1, 1, 1)), is(asSet(1)));
    }

    @Test
    public void duplicatesAndNonDuplicatesMixed() {
        assertThat(duplicateValues(asList(0, 1, 2, 3, 3, 2)), is(asSet(2, 3)));
    }

    private Set<Integer> asSet(Integer... is) {
        return new HashSet<>(asList(is));
    }
}
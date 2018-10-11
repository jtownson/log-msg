package net.jtownson.annotation;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FormatHashMapTest {

    @Test
    public void shouldFindFormatStringRegexps() {
        // given
        String format = "Something went wrong with %s and %s.";
        String expansion = "Something went wrong with the app and the libraries.";

        // when
        FormatHashMap map = new FormatHashMap();
        map.put(format, "A value");

        // then
        assertThat(map.get(expansion), is("A value"));
    }

    @Test
    @Ignore("Is this is needed?")
    public void shouldWorkWithAngleBrackets() {
        // given
        String format = "Row number (excluding header rows): {}, cause: {}";
        String expansion = "Row number (excluding header rows): 3, cause: foo cause";

        // when
        FormatHashMap map = new FormatHashMap();
        map.put(format, "A value");

        // then
        assertThat(map.get(expansion), is("A value"));
    }

    @Test
    public void shouldFindAComplexFormat() {
        // given
        String format = "Duke's Birthday: %1$tm %1$te,%1$tY";
        String expansion = String.format(format, Calendar.getInstance());

        // when
        FormatHashMap map = new FormatHashMap();
        map.put(format, "A value");

        // then
        assertThat(map.get(expansion), is("A value"));
    }

    @Test
    public void shouldFindFormatStringsWithSpecialRegexCharsEmbedded() {
        // given
        String format = "Something went wrong - [ with %s and %s.";
        String expansion = "Something went wrong - [ with the app and the libraries.";

        // when
        FormatHashMap map = new FormatHashMap();
        map.put(format, "A value");

        // then
        assertThat(map.get(expansion), is("A value"));
    }
}
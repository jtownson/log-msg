package net.jtownson.annotation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FormatConverterTest {

    @Test
    public void shouldMatchADecimalFormatSpecifier() {
        // given
        String format = "The temperature is '%d'";
        String expansion = "The temperature is '1.1234'";

        // when
        Matcher matcher = Pattern.compile(FormatConverter.convert(format)).matcher(expansion);

        // then
        assertThat(matcher.matches(), is(true));
        assertThat(matcher.group(), is(expansion));
        assertThat(matcher.group(1), is("1.1234"));
    }

    @Test
    public void shouldSplitADecimalFormatSpecifier() {
        // given
        String format = "The temperature is '%d'";

        // when
        List<String> l = FormatConverter.split(format);

        // then
        assertThat(l, is(asList("The temperature is '", "'")));
    }

    @Test
    public void shouldSplitAMixedFormatString() {
        // given
        String format = "The temperature in %s is %d";

        // when
        List<String> l = FormatConverter.split(format);

        // then
        assertThat(l, is(asList("The temperature in ", " is ")));
    }

    @Test
    public void shouldMatchAComplexDateSpecifier() {
        // given
        String format ="Something happened at %1$tm %1$te,%1$tY";
        String runtimeMessage = "Something happened at May 23, 1995";

        // when
        Matcher matcher = Pattern.compile(FormatConverter.convert(format)).matcher(runtimeMessage);

        // then
        assertThat(matcher.matches(), is(true));
    }
}
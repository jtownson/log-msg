package net.jtownson.annotation;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MessageHolderTest {

    @Test
    public void shouldParseAMessage() {
        MessageHolder h = new MessageHolder("ODS-1234");
        assertThat(h.prefix(), is("ODS"));
        assertThat(h.sequenceNumber(), is(1234));
    }
}
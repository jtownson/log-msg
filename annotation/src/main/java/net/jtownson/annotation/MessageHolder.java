package net.jtownson.annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHolder {
    private final String messageId;
    private final String prefix;
    private final int sequenceNumber;

    static final Pattern messageIdPattern = Pattern.compile("(\\w+)\\-(\\d+)");

    public MessageHolder(String messageId) {
        this.messageId = messageId;
        Matcher matcher = messageIdPattern.matcher(messageId);
        if (matcher.matches()) {
            prefix = matcher.group(1);
            sequenceNumber = Integer.valueOf(matcher.group(2));
        } else {
            throw new IllegalArgumentException(
                    "The format of the message id '%s' is not valid. " +
                    "Please use digits and numbers in the format ABCD-1234 " +
                    "(as in ORA-1979, ODS-0001, DBOS-1234, etc)");
        }
    }

    public String prefix() {
        return prefix;
    }

    public int sequenceNumber() {
        return sequenceNumber;
    }

    public String messageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MessageHolder that = (MessageHolder) o;

        return new EqualsBuilder()
                .append(sequenceNumber, that.sequenceNumber)
                .append(prefix, that.prefix)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(prefix)
                .append(sequenceNumber)
                .toHashCode();
    }
}

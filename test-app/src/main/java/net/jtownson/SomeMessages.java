package net.jtownson;

import net.jtownson.annotation.Message;

public interface SomeMessages {

    @Message("APP-0004")
    String logMessage = "Interface message";

    @Message("APP-0005")
    String logMessage2 = "Another message";

    @Message("APP-0006")
    String newMessage = "A new message";
}

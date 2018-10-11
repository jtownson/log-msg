package net.jtownson;

import net.jtownson.annotation.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AService {

    @Message("APP-0000")
    private static final String msg = "foo";
    @Message("APP-0001")
    private static final String iMsg1 = "Hello, world -- instance level";
    @Message("APP-0002")
    private static final String iMsg2 = "whatchew doing -- instance level";
    @Message("APP-0003")
    private static final String msg3 = "a final message";

    @Message("APP-0010")
    private static final String msg10 = "message ten with format: %s";

    @Message("APP-0011")
    private static final String msg11 = "Exception occurred with {} lookup.";


    Logger log = LoggerFactory.getLogger(AService.class);

    public String doSomething() {
        log.info(iMsg1);
        log.info(iMsg2);
        String msg3 = "An unknown message";
        log.info(msg3);
        log.info(SomeMessages.logMessage);

        log.error(SomeMessages.newMessage);

        log.error(String.format(msg10, "foo-format"));

        log.error(msg11, "bdr");
        return "something";
    }
}

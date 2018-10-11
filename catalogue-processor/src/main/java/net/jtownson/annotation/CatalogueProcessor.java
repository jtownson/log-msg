package net.jtownson.annotation;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes({"net.jtownson.annotation.Message"})
public class CatalogueProcessor extends AbstractProcessor {

    public static final HashSet<String> supportedTypes = new HashSet<>(asList(Message.class.getCanonicalName()));

    private CoreMessageProcessor coreMessageProcessor;
    private Messager messager;
    private int round = 0;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        coreMessageProcessor = new CoreMessageProcessor(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (round != 0) {
            return false;
        }

        try {
            Map<String, MessageHolder> messageMap = coreMessageProcessor.process(roundEnv);

            HashMap<MessageHolder, String> reverseMessageMap = reverse(messageMap);

            List<MessageHolder> messageKeys = sortedMessageIds(messageMap);

            Path path = Paths.get("log-message-report.txt");
            try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {

                writer.write("||Message ID||Message Text||Confluence Link||\n");

                messageKeys.forEach(messageKey -> {
                    String message = reverseMessageMap.get(messageKey);
                    String messageId = messageKey.messageId();
                    String pageTitle = String.format("%s error description", messageId);
                    String link = "https://<page>";

                    try {
                        writer.write(String.format("|%s|%s|%s|\n", messageId, message, link));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "Log catalogue written to " + path);
            ++round;
            return true;

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failure during annotation processing on @Message: %s", e));
            return false;
        }
    }

    private String encode(String pageTitle) {
        try {
            return URLEncoder.encode(pageTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    static List<MessageHolder> sortedMessageIds(Map<String, MessageHolder> messageMap) {
        List<MessageHolder> l = new ArrayList<>(messageMap.values());
        Collections.sort(l, comparing(MessageHolder::messageId));
        return l;
    }

    static <K,V> HashMap<V,K> reverse(Map<K,V> map) {
        HashMap<V,K> rev = new HashMap<>();
        for(Map.Entry<K,V> entry : map.entrySet())
            rev.put(entry.getValue(), entry.getKey());
        return rev;
    }
}

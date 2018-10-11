package net.jtownson.annotation;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.SourceVersion.latestSupported;

@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes({"net.jtownson.annotation.Message"})
public class MessageAnnotationProcessor extends AbstractProcessor {

    public static final HashSet<String> supportedTypes = new HashSet<>(asList(Message.class.getCanonicalName()));

    private CoreMessageProcessor coreMessageProcessor;
    private Messager messager;
    private CodeGenerator codeGenerator;
    private int round = 0;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        coreMessageProcessor = new CoreMessageProcessor(processingEnv);
        codeGenerator = new CodeGenerator(processingEnv.getFiler());
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

            codeGenerator.generate(messageMap);

            ++round;
            return true;

        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failure during annotation processing on @Message: %s", e));
            return false;
        }
    }
}

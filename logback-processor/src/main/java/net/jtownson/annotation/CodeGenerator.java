package net.jtownson.annotation;


import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Map;

class CodeGenerator {

    private final Filer filer;

    CodeGenerator(Filer filer) {
        this.filer = filer;
    }

    boolean generate(Map<String, MessageHolder> messages) throws IOException {

        ParameterizedTypeName formatHashMap = ParameterizedTypeName.get(FormatHashMap.class, MessageHolder.class);

        FieldSpec msgMap = FieldSpec.builder(formatHashMap, "msgMap", Modifier.STATIC, Modifier.FINAL)
                .initializer("new FormatHashMap<>()", FormatHashMap.class)
                .build();

        MethodSpec convertMethod = MethodSpec
                .methodBuilder("convert")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addParameter(ILoggingEvent.class, "event")
                .addCode("$T msgHolder = ($T)msgMap.get(event.getMessage());\n", MessageHolder.class, MessageHolder.class)
                .addCode("return msgHolder == null ? \"\" : msgHolder.messageId();\n")
                .build();

        TypeSpec messageIdConverter = TypeSpec.classBuilder("LogbackMessageIdConverter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassicConverter.class)
                .addField(msgMap)
                .addMethod(convertMethod)
                .addStaticBlock(messageMapInitializer(messages))
                .build();

        JavaFile javaFile = JavaFile.builder("net.jtownson.logback", messageIdConverter).build();
        javaFile.writeTo(filer);
        return true;
    }

    private CodeBlock messageMapInitializer(Map<String, MessageHolder> messages) {
        CodeBlock.Builder builder = CodeBlock.builder();
        messages.forEach(
                (message, messageHolder) ->
                builder.add(String.format("msgMap.put(\"%s\", new MessageHolder(\"%s\"));\n", message, messageHolder.messageId()))
        );
        return builder.build();
    }
}

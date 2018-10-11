package net.jtownson.annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.*;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;

class CoreMessageProcessor {

    private final ProcessingEnvironment processingEnvironment;

    public CoreMessageProcessor(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
    }

    public Map<String, MessageHolder> process(RoundEnvironment roundEnv) {

        Map<String, MessageHolder> messageMap = new HashMap<>();

        Set<MessageHolder> messagesUsed = new HashSet<>();

        Set<VariableElement> fields = getAnnotatedFields(roundEnv);

        for (VariableElement field : fields) {

            String value = getValueAsString(field);

            checkValue(field, value);

            Message annotation = field.getAnnotation(Message.class);
            MessageHolder messageHolder = new MessageHolder(annotation.value());

            if (messagesUsed.contains(messageHolder)) {
                error(field,
                        "Message identifier '%s' is a duplicate. ", messageHolder.messageId());
                throw new IllegalStateException();
            }
            messagesUsed.add(messageHolder);
            messageMap.put(value, messageHolder);
        }

        List<Integer> sequenceValues = sequenceValues(messageMap);
        List<Integer> spareIds = spareIds(sequenceValues);

        if (!spareIds.isEmpty()) {
            note(null, "Compiled list of message identifiers is non-contiguous. Gaps are %s", spareIds);
        }

        note(null, "Next available messageId is '%s'", nextAvailableId(sequenceValues));

        return messageMap;
    }

    private void checkValue(VariableElement field, String value) {
        if (value == null) {
            error(field,
                    "Field %s has no available constant value. Check that it is declared as final.",
                    field.getSimpleName());
            throw new IllegalStateException();
        }
    }

    private Set<VariableElement> getAnnotatedFields(RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Message.class);
        return ElementFilter.fieldsIn(annotatedElements);
    }

    private List<Integer> sequenceValues(Map<String, MessageHolder> messages) {
        return messages.values().stream().map(MessageHolder::sequenceNumber).collect(toList());
    }

    private Integer nextAvailableId(List<Integer> messages) {
        return messages.isEmpty() ? 0 : messages.stream().max(naturalOrder()).get() + 1;
    }

    private List<Integer> spareIds(List<Integer> messages) {
        return MissingValues.missingValues(messages);
    }

    private String getValueAsString(VariableElement field) {

        checkDeclaredType(field);

        Object constantValue = field.getConstantValue();

        if (constantValue == null) {
            error(field,
                    "The field %s is not declared with a final, constant value. " +
                    "Did you forget to declare it static final?", field);
            return "";
        }

        return String.valueOf(constantValue);
    }

    private void checkDeclaredType(VariableElement field) {
        String aClass = field.asType().toString();
        if ( ! "java.lang.String".equals(aClass)) {
            warn(field, "The field %s should be declared as String (but is %s)", field.asType().toString(), aClass);
        }
    }

    private void error(Element e, String msg, Object... args) {
        print(Diagnostic.Kind.ERROR, e, String.format(msg, args), e);
    }

    private void warn(Element e, String msg, Object... args) {
        print(Diagnostic.Kind.WARNING, e, String.format(msg, args), e);
    }

    private void note(Element e, String msg, Object... args) {
        print(Diagnostic.Kind.NOTE, e, String.format(msg, args), e);
    }

    private void print(Diagnostic.Kind kind, Element e, String msg, Object... args) {
        processingEnvironment.getMessager().printMessage(kind, String.format("MessageAnnotationProcessor: " + msg, args), e);
    }
}

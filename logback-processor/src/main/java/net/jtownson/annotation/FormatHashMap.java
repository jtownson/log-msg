package net.jtownson.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FormatHashMap<V> extends HashMap<String, V> {

    private HashMap<Pattern, V> regExPatterns = new HashMap<>();

    @Override
    public V put(String key, V value) {
        Pattern pattern = Pattern.compile(FormatConverter.convert(key));
        regExPatterns.put(pattern, value);
        return value;
    }

    @Override
    public V get(Object key) {
        CharSequence cs = key.toString();
        for (Map.Entry<Pattern, V> entry : regExPatterns.entrySet()) {
            Pattern pattern = entry.getKey();
            if (pattern.matcher(cs).matches()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
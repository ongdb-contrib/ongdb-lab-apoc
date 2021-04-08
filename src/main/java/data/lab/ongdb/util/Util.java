package data.lab.ongdb.util;

import data.lab.ongdb.OlabConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import javax.lang.model.SourceVersion;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util.Util
 * @Description: TODO
 * @date 2021/4/2 15:55
 */
public class Util {

    public static final Label[] NO_LABELS = new Label[0];

    public static String labelString(List<String> labelNames) {
        return labelNames.stream().map(Util::quote).collect(Collectors.joining(":"));
    }

    public static <T> Map<String, T> map(T ... values) {
        Map<String, T> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i+=2) {
            if (values[i] == null) {
                continue;
            }
            map.put(values[i].toString(),values[i+1]);
        }
        return map;
    }

    public static String quote(String var) {
        return SourceVersion.isIdentifier(var) ? var : '`' + var + '`';
    }

    public static List<String> labelStrings(Node n) {
        return StreamSupport.stream(n.getLabels().spliterator(), false).map(Label::name).sorted().collect(Collectors.toList());
    }

    public static Label[] labels(Object labelNames) {
        if (labelNames == null) {
            return NO_LABELS;
        }
        if (labelNames instanceof List) {
            // Removing duplicates
            Set names = new LinkedHashSet((List) labelNames);
            Label[] labels = new Label[names.size()];
            int i = 0;
            for (Object l : names) {
                if (l == null) {
                    continue;
                }
                labels[i++] = Label.label(l.toString());
            }
            if (i <= labels.length) {
                return Arrays.copyOf(labels, i);
            }
            return labels;
        }
        return new Label[]{Label.label(labelNames.toString())};
    }

    public static Optional<String> getLoadUrlByConfigFile(String loadType, String key, String suffix){
        key = Optional.ofNullable(key).map(s -> s + "." + suffix).orElse(StringUtils.EMPTY);
        Object value = OlabConfiguration.get(loadType).get(key);
        return Optional.ofNullable(value).map(Object::toString);
    }

    public static Map<String, Object> subMap(Map<String, ?> params, String prefix) {
        Map<String, Object> config = new HashMap<>(10);
        int len = prefix.length() + (prefix.isEmpty() || prefix.endsWith(".") ? 0 : 1);
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                config.put(key.substring(len), entry.getValue());
            }
        }
        return config;
    }

    public static boolean toBoolean(Object value) {
        if ((value == null || value instanceof Number && (((Number) value).longValue()) == 0L || value instanceof String && (value.equals("") || ((String) value).equalsIgnoreCase("false") || ((String) value).equalsIgnoreCase("no")|| ((String) value).equalsIgnoreCase("0"))|| value instanceof Boolean && value.equals(false))) {
            return false;
        }
        return true;
    }
}

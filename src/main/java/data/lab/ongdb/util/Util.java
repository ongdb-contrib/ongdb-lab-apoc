package data.lab.ongdb.util;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    public static List<String> labelStrings(Node n) {
        return StreamSupport.stream(n.getLabels().spliterator(), false).map(Label::name).sorted().collect(Collectors.toList());
    }

    public static Label[] labels(Object labelNames) {
        if (labelNames == null) {
            return NO_LABELS;
        }
        if (labelNames instanceof List) {
            Set names = new LinkedHashSet((List) labelNames); // Removing duplicates
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
}

package data.lab.ongdb.schema;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema
 * @Description: TODO
 * @date 2021/3/24 17:20
 */
public class TripleData {
    private String from;
    private String rel;
    private String to;

    public TripleData() {
    }

    public TripleData(String from, String rel, String to) {
        this.from = from;
        this.rel = rel;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "(" + from + ")-[" + rel + "]->(" + to + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TripleData that = (TripleData) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(rel, that.rel) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, rel, to);
    }
}

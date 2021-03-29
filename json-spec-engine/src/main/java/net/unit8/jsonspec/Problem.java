package net.unit8.jsonspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Problem implements Serializable {
    private final String[] path;
    private final String predicate;
    private final Object value;
    private final String reason;
    private final String[] via;
    private final String[] in;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Problem(@JsonProperty("path") String[] path,
                   @JsonProperty("pred") String predicate,
                   @JsonProperty("val") Object value,
                   @JsonProperty("reason") String reason,
                   @JsonProperty("via") String[] via,
                   @JsonProperty("in" )String[] in) {
        this.path = path;
        this.predicate = predicate;
        this.value = value;
        this.reason = reason;
        this.via = via;
        this.in = in;
    }

    public String[] getPath() {
        return path;
    }

    public String getPredicate() {
        return predicate;
    }

    public Object getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }

    public String[] getVia() {
        return via;
    }

    public String[] getIn() {
        return in;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "path=" + Arrays.toString(path) +
                ", predicate='" + predicate + '\'' +
                ", value=" + value +
                (reason != null ? ", reason='" + reason + '\'' : "") +
                ", via='" + Arrays.toString(via) + '\'' +
                ", in=" + Arrays.toString(in) +
                '}';
    }
}

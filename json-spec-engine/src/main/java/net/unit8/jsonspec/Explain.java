package net.unit8.jsonspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Explain implements Serializable {
    private final List<Problem> problems;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Explain(@JsonProperty("problems") List<Problem> problems) {
        this.problems = problems;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    @Override
    public String toString() {
        return "Explain{" +
                "problems=" + problems +
                '}';
    }
}

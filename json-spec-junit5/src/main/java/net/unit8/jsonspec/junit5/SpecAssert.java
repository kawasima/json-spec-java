package net.unit8.jsonspec.junit5;

import net.unit8.jsonspec.Explain;
import net.unit8.jsonspec.SpecEngine;

/**
 * Assert an object that confirms to the given specification.
 *
 * @author kawasima
 */
public class SpecAssert<T>{
    private final T actual;
    private final SpecEngine specEngine;

    public SpecAssert(T actual, SpecEngine specEngine) {
        this.actual = actual;
        this.specEngine = specEngine;
    }

    /**
     * Check if the object conforms to the specification.
     *
     * @param specName the name of the specification
     */
    public void conformTo(String specName) {
        specEngine.explain(specName, actual).ifPresent(this::throwAssertionError);
    }

    private void throwAssertionError(Explain explain) {
        throw new AssertionError(explain.getProblems().toString());
    }
}

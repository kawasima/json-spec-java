package net.unit8.jsonspec.junit5;

import net.unit8.jsonspec.SpecEngine;

/**
 * @author kawasima
 */
public class SpecAssertions {
    private static final SpecEngine specEngine = SpecEngine.getDefault();

    private SpecAssertions() {

    }

    /**
     * Create an assertion.
     *
     * @param actual the target object to assert
     * @return An assertion has the given object.
     */
    public static <T> SpecAssert<T> assertThat(T actual){
        return new SpecAssert<>(actual, specEngine);
    }
}

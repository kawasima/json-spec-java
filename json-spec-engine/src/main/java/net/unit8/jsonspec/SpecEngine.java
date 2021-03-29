package net.unit8.jsonspec;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A spec engine.
 *
 * @author kawasima
 */
public interface SpecEngine extends AutoCloseable {
    /**
     * Get the default implementation of a spec engine.
     *
     * @return The default implementation of a spec engine.
     */
    static SpecEngine getDefault() {
        return SpecEngineImpl.getInstance();
    }

    /**
     * Load the given spec file.
     *
     * @param specFile the spec file
     */
    void load(File specFile);

    /**
     * Generate data by the given specification.
     *
     * @param specName the name of the specification
     * @param clazz the class that will be generated
     * @param <T> the type of the class
     * @return the object stream of conformed to the specification.
     */
    <T> Stream<T> generate(String specName, Class<T> clazz);

    /**
     * Investigate whether the given object conforms to the specification.
     *
     * @param specName the name of the specification
     * @param value the object to validate
     * @param <T> the type of the value
     * @return true if the value conforms to the specification
     */
    <T> boolean isValid(String specName, T value);

    /**
     * Explain that the given object conforms to the specification.
     *
     * @param specName the name of the specification
     * @param value the object to explain
     * @return The result of the explanation
     */
    <T> Optional<Explain> explain(String specName, T value);
}

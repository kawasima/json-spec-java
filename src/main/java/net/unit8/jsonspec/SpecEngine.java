package net.unit8.jsonspec;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public interface SpecEngine extends AutoCloseable {
    void load(File specFile) throws IOException;
    <T> Stream<T> generate(String specName, Class<T> clazz);

    static SpecEngine getDefault() {
        return new SpecEngineImpl();
    }
}

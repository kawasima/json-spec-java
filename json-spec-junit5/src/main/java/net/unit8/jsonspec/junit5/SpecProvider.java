package net.unit8.jsonspec.junit5;

import net.unit8.jsonspec.SpecEngine;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Provides arguments to the test method.
 *
 * @author kawasima
 */
public class SpecProvider implements ArgumentsProvider, AnnotationConsumer<SpecSource> {
    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private int sample;
    private String[] specName;
    private final SpecEngine specEngine;

    public SpecProvider() {
        specEngine = SpecEngine.getDefault();
    }

    /**
     * Provide a stream of arguments.
     *
     * @param context the context of the extension
     * @return a stream of arguments
     * @throws Exception in case of some errors
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Method method = context.getRequiredTestMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        int size = Math.min(specName.length, method.getParameterCount());
        List<Iterator<?>> specIterators = IntStream.range(0, size).mapToObj(i->
                specEngine.generate(specName[i], parameterTypes[i]).iterator()
        ).collect(Collectors.toUnmodifiableList());

        return Stream.generate(() -> Arguments.of(
                specIterators.stream().map(Iterator::next).toArray()))
                .limit(sample);
    }

    /**
     * Accept SpecSource annotation.
     *
     * @param specSource the configuration of the specification source.
     */
    @Override
    public void accept(SpecSource specSource) {
        Stream.of(specSource.uri())
                .map(this::getURI)
                .map(File::new)
                .forEach(specEngine::load);
        specName = specSource.spec();
        sample = specSource.sample();
    }

    private URI getURI(String resourceLocation) {
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
                if (url == null) {
                    throw new FileNotFoundException(path + " cannot be resolved to absolute file path because it does not exist");
                }
                return url.toURI();
            } catch (URISyntaxException | FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return URI.create(resourceLocation);
    }
}

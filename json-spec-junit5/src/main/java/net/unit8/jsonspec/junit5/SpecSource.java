package net.unit8.jsonspec.junit5;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;
import java.net.URI;

/**
 * Declare a specification source.
 * This annotation should be used with ParameterizedTest.
 *
 * @author kawasima
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(SpecProvider.class)
public @interface SpecSource {
    /**
     * The file path of the specification.
     */
    String[] uri();

    /**
     * The name of the specification.
     */
    String[] spec();


    /**
     * The sampling number.
     */
    int sample() default 10;
}

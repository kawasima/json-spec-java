package net.unit8.jsonspec;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

public class SpecEngineTest {
    @Test
    void test1() throws Exception {
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            specEngine.load(new File("src/test/resources/spec.js"));
            Stream<Person> personStream = specEngine.generate("person", Person.class);
            personStream.limit(100).forEach(System.out::println);
        }
    }
}

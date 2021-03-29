package net.unit8.jsonspec;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class SpecEngineTest {
    @Test
    void generation() throws Exception {
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            specEngine.load(new File("src/test/resources/spec.js"));
            Stream<Person> personStream = specEngine.generate("person", Person.class);
            personStream.limit(100).forEach(System.out::println);
        }
    }

    @Test
    void explain() throws Exception {
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            specEngine.load(new File("src/test/resources/spec.js"));
            Person person = new Person("Yoshitaka", "Kawashima", List.of("Clojure"), new Date(1974, Calendar.OCTOBER, 16), "1111111");
            System.out.println(specEngine.explain("person", person));
        }
    }

}

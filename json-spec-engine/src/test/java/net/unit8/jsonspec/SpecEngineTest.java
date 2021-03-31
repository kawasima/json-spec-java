package net.unit8.jsonspec;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SpecEngineTest {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");
        Arrays.stream(Logger.getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.ALL));
        Logger.getLogger("").setLevel(Level.FINER);

    }
    @Test
    void generation() throws Exception {
        File baseDir = new File(SpecEngineTest.class.getResource("/").toURI()).getAbsoluteFile();
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            specEngine.load(new File(baseDir, "spec.js"));
            Stream<Person> personStream = specEngine.generate("person", Person.class);
            personStream.limit(100).forEach(System.out::println);
        }
    }

    @Test
    void explain() throws Exception {
        File baseDir = new File(SpecEngineTest.class.getResource("/").toURI()).getAbsoluteFile();
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            Logger.getLogger("").finer("Start loading a spec file");
            specEngine.load(new File(baseDir, "spec.js"));
            Logger.getLogger("").finer("End loading a spec file");
            Person person = new Person("Yoshitaka", "Kawashima", List.of("Clojure"), new Date(1974, Calendar.OCTOBER, 16), "1111111");
            System.out.println(specEngine.explain("person", person));
        }
    }

}

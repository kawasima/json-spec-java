package net.unit8.jsonspec;

import net.unit8.jsonspec.junit5.SpecAssertions;
import net.unit8.jsonspec.junit5.SpecSource;
import org.junit.jupiter.params.ParameterizedTest;

public class SpecValueSourceTest {
    @ParameterizedTest
    @SpecSource(spec = "person",  uri = "classpath:spec.js")
    void testSpec(Person person) {
        SpecAssertions.assertThat(person.getFullName()).conformTo("fullName");
    }
}

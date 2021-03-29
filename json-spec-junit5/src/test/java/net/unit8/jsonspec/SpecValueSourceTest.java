package net.unit8.jsonspec;

import net.unit8.jsonspec.junit5.SpecAssertions;
import net.unit8.jsonspec.junit5.SpecSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SpecValueSourceTest {
    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void test(String s) {
        System.out.println(s);
    }

    @ParameterizedTest
    @SpecSource(spec = "person", file ="src/test/resources/spec.js")
    void testSpec(Person person) {
        SpecAssertions.assertThat(person.getFullName()).conformTo("fullName");
    }
}

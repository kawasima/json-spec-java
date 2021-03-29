# json-spec-java

json-spec-java is a Java binding of the [json-spec](https://github.com/kawasima/json-spec) library.

## Getting started

Add the dependency to your pom.xml.

```xml
<dependency>
    <groupId>net.unit8.jsonspec</groupId>
    <artifactId>json-spec-engine</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

And install json-spec using npm command.

```json
{
  "version": "0.1.0-SNAPSHOT",
  "private": true,
  "dependencies": {
    "@json-spec/core": "^0.1.10",
    "@json-spec/jest-matcher": "^0.1.10",
    "@json-spec/spec-basic": "^0.1.10",
    "@json-spec/spec-profiles": "^0.1.10",
    "@json-spec/spec-range": "^0.1.10",
    "assert": "^2.0.0"
  }
}
```

```shell
% npm install
```

The simplest use of json-spec-java is as follows:

```java
public class Example {
    public void example() {
        try (SpecEngine specEngine = SpecEngine.getDefault()) {
            specEngine.load(new File("./spec.js"));
            Stream<Person> personStream = specEngine.generate("person", Person.class);
            personStream.limit(100).forEach(System.out::println);
        }
    }
}
```

The parameterized test using json-spec-java is as follows:

```java
public class SimpleTest {
    @ParameterizedTest
    @SpecSource(spec = "person", file = "./spec.js")
    void testSpec(Person person) {
        SpecAssertions.assertThat(person.getFullName()).conformTo("fullName");
    }
}
```

## LICENSE

Copyright © 2021 kawasima

Distributed under the Eclipse Public License either version 2.0 or (at your option) any later version.



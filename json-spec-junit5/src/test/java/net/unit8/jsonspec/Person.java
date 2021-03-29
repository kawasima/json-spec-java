package net.unit8.jsonspec;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Person {
    String firstName;
    String lastName;
    List<String> languages;
    Date birthDay;
    String postalCd;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

package rip.alpha.libraries.tests;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestData {

    private final String id;
    private final ComplexNestedData nestedData = new ComplexNestedData(UUID.randomUUID(), Date.from(Instant.now()));
    private String stringData = "";
    private int intData = 0;
    private double doubleData = 0.0D;

    public record ComplexNestedData(UUID someID, Date date) {
    }

}

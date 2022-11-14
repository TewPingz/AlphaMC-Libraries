package rip.alpha.libraries.util.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameIDEntry {

    private String name;
    private UUID uuid;

}

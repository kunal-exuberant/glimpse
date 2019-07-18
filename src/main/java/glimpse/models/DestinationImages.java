package glimpse.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DestinationImages {
    private List<String> images = new ArrayList<>();
}

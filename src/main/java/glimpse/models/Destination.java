package glimpse.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Destination implements Serializable {
    public static int DESTINATION_ID;
    private int id;
    private String name;
    private Type type;
    private int distance;
    private String description;
    private Address address;
    private List<String> images;

    public Destination(String name){
        this.id = ++DESTINATION_ID;
        this.name = name;
    }

    public Destination(int id, String name){
        this.id = id;
        this.name = name;
    }
}
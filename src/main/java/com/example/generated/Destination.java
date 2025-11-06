import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Destination {
    private String name;
    private String country;

    public Destination(String name, String country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Destination{name='" + name + "', country='" + country + "'}";
    }
}

class TravelManager {
    private List<Destination> destinations;

    public TravelManager() {
        this.destinations = new ArrayList<>();
    }

    public void addDestination(String name, String country) {
        Destination newDestination = new Destination(name, country);
        destinations.add(newDestination);
        System.out.println("Added: " + newDestination);
    }

    public void listDestinations() {
        System.out.println("Travel Destinations:");
        for (Destination destination : destinations) {
            System.out.println(destination);
        }
    }
}

public class TravelApp {
    public static void main(String[] args) {
        TravelManager travelManager = new TravelManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter a new travel destination (name, country) or type 'exit' to quit:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] parts = input.split(",");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String country = parts[1].trim();
                travelManager.addDestination(name, country);
            } else {
                System.out.println("Invalid input. Please enter in the format: name, country");
            }
        }

        travelManager.listDestinations();
        scanner.close();
    }
}
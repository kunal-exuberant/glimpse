package glimpse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import glimpse.models.Destination;
import glimpse.models.Duration;
import glimpse.models.Type;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Path("/destination")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DestinationResource {

    private DestinationService destinationService;

    @Inject
    public DestinationResource(DestinationService destinationService){
        this.destinationService = destinationService;
        ESOperations.getClient();
    }

    @Path("/{id}")
    @GET
    public Destination getDestinationInfo(@PathParam("id") String id) throws JsonProcessingException{
        System.out.println("I am inside get destination info");

        Optional<Destination> optionalDestination = destinationService.getDestination(id);
        return optionalDestination.get();
    }

    @Path("/search/{q}")
    @GET
    public List<Destination> searchDestination(@PathParam("q") String q) throws JsonProcessingException{
        System.out.println("I am inside get destination search "+q);
        return destinationService.searchDestination(q);
    }

    @Path("/recommend/{duration}")
    @GET
    public List<Destination> getDestinations(@PathParam("duration") Duration duration){
        System.out.println("I am inside recommend destination "+duration);
        return destinationService.recommendDestination(duration, Arrays.asList(Type.TREKKING));
    }
}

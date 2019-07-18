package glimpse;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GlimpseApp extends Application<GlimpseConfig> {
    private static String RESOURCE_PATH = "Glimpse.resources";
    public static void main(String[] args) throws Exception {
        System.out.println("Travel destination app started");
        new GlimpseApp().run(args);
        System.out.println("Travel destination has started");
    }

    @Override
    public void initialize(Bootstrap<GlimpseConfig> bootstrap) {
        System.out.println("Inside initialize method");
        bootstrap.addBundle(new AssetsBundle("/assets","/", "index.html", "assets"));

        GuiceBundle.Builder<GlimpseConfig> guiceBundleBuilder = GuiceBundle.newBuilder();

        GuiceBundle guiceBundle = guiceBundleBuilder
                .setConfigClass(GlimpseConfig.class)
                .addModule(new GlimpseModule())
                .build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(GlimpseConfig configuration, Environment environment) throws Exception {
        System.out.println("Inside run method");
        final DestinationResource travelDestinationResource = new DestinationResource(new DestinationService(new DataStore()));
        environment.jersey().register(travelDestinationResource);
        environment.jersey().setUrlPattern("/glimpse/*");
    }
}

package glimpse;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GlimpseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DestinationResource.class).in(Singleton.class);
    }
}

package micronaut.request.scope;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;

@Factory
public class CustomContextFactory
{

    @Bean
    @Context
    public CustomContext<RequestMetadata> customContext()
    {
        return new CustomContextImpl<>();
    }
}

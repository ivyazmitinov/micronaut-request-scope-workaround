package micronaut.request.scope;

import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

@Context
@Controller
public class TestController
{
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    @Inject
    CustomContext<RequestMetadata> customContext;

    @Get("/test")
    public Mono<MutableHttpResponse<String>> test()
    {
        return Mono.subscriberContext()
                   .doOnNext(c -> LOG.info(c.get(RequestMetadata.class).getAppName()))
                   .map(c -> {
                       // Very important logic
                       return HttpResponse.ok(c.get(RequestMetadata.class).getAppName());
                   })
                   .subscriberContext(c -> c.put(RequestMetadata.class, customContext.currentContext().get()
                                                                            .getAppName()));
    }
}

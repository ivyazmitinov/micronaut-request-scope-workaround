package micronaut.request.scope;

import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.inject.Inject;

@Context
@Controller
public class TestController
{
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    @Inject
    MyReactiveThreadLocal myReactiveThreadLocal;

    @Get("/test")
    public Mono<MutableHttpResponse<?>> test()
    {
        return Mono.just(1)
                   .flatMap(ignored ->
                                Mono.just(1)
                                    .doOnNext(l -> logFromContext())
                                    .flatMap(ignored2 ->
                                                 Mono.just(2)
                                                     .doOnNext(l -> logFromContext())
                                                     .subscribeOn(Schedulers.elastic()))
                                    .doOnNext(l -> logFromContext())
                                    .subscribeOn(Schedulers.elastic()))
                   .doOnNext(l -> logFromContext())
                   .map(HttpResponse::ok);
    }

    private void logFromContext()
    {
        LOG.info(myReactiveThreadLocal.getAppName());
    }
}

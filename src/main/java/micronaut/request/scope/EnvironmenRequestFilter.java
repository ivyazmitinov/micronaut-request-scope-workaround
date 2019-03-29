package micronaut.request.scope;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

@Filter("/**")
public class EnvironmenRequestFilter implements HttpFilter
{

    private static final String APP_NAME_HEADER = "APP_NAME";
    private static final String ERROR_MESSAGE = String.format("%s Header not specified", APP_NAME_HEADER);

    @Inject
    private MyReactiveThreadLocal myReactiveThreadLocal;

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain)
    {

        return Mono.fromCallable(() -> request.getHeaders().get(APP_NAME_HEADER))
                   .doOnNext(s -> myReactiveThreadLocal.setAppName(s))
                   .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalStateException(ERROR_MESSAGE))))
                   .flatMapMany(s -> chain.proceed(request));
    }
}

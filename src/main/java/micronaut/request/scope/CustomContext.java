package micronaut.request.scope;

import java.util.Optional;
import java.util.concurrent.Callable;

public interface CustomContext<C>
{
    Optional<C> currentContext();
    void with(C context, Runnable runnable);
    <T> T with(C context, Callable<T> callable) throws Exception;
}

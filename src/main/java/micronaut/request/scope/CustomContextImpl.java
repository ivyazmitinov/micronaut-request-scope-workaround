package micronaut.request.scope;

import java.util.Optional;
import java.util.concurrent.Callable;

public class CustomContextImpl<C> implements CustomContext<C>
{
    private final ThreadLocal<C> contextHolder = new ThreadLocal<>();

    @Override
    public Optional<C> currentContext()
    {
        return Optional.ofNullable(contextHolder.get());
    }

    @Override
    public void with(C context, Runnable runnable)
    {
        C existing = contextHolder.get();
        boolean isSet = false;
        try
        {
            if (context != existing)
            {
                isSet = true;
                contextHolder.set(context);
            }
            runnable.run();
        }
        finally
        {
            if (isSet)
            {
                contextHolder.remove();
            }
        }
    }

    @Override
    public <T> T with(C context, Callable<T> callable) throws Exception
    {
        C existing = contextHolder.get();
        boolean isSet = false;
        try
        {
            if (context != existing)
            {
                isSet = true;
                contextHolder.set(context);
            }
            return callable.call();
        }
        finally
        {
            if (isSet)
            {
                contextHolder.remove();
            }
        }
    }
}

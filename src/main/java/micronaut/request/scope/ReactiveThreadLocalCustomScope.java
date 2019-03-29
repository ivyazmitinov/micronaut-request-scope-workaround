package micronaut.request.scope;

import io.micronaut.context.BeanResolutionContext;
import io.micronaut.context.LifeCycle;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.context.scope.CustomScope;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.BeanIdentifier;
import io.micronaut.scheduling.instrument.InstrumentedScheduledExecutorService;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
public class ReactiveThreadLocalCustomScope implements CustomScope<ReactiveThreadLocal>,
                                                       LifeCycle<ReactiveThreadLocalCustomScope>,
                                                       BeanCreatedEventListener<ScheduledExecutorService>
{

    private final java.lang.ThreadLocal<Map<String, Object>> threadScope =
        java.lang.ThreadLocal.withInitial(HashMap::new);

    @Override
    public Class<ReactiveThreadLocal> annotationType()
    {
        return ReactiveThreadLocal.class;
    }

    @Override
    public <T> T get(BeanResolutionContext resolutionContext, BeanDefinition<T> beanDefinition,
                     BeanIdentifier identifier, Provider<T> provider)
    {
        Map<String, Object> values = threadScope.get();
        String key = identifier.toString();
        T bean = (T) values.get(key);
        if (bean == null)
        {
            bean = provider.get();
            values.put(key, bean);
        }
        return bean;
    }

    @Override
    public <T> Optional<T> remove(BeanIdentifier identifier)
    {
        Map<String, Object> values = threadScope.get();
        T previous = (T) values.remove(identifier.toString());
        return previous != null ? Optional.of(previous) : Optional.empty();
    }

    @Override
    public boolean isRunning()
    {
        return true;
    }

    @Override
    public ReactiveThreadLocalCustomScope start()
    {
        return this;
    }

    @Override
    public ReactiveThreadLocalCustomScope stop()
    {
        threadScope.remove();
        return this;
    }

    @Override
    public ScheduledExecutorService onCreated(BeanCreatedEvent<ScheduledExecutorService> event)
    {
        return new InstrumentedScheduledExecutorService()
        {
            @Override
            public ScheduledExecutorService getTarget()
            {
                return event.getBean();
            }

            @Override
            public <T> Callable<T> instrument(Callable<T> task)
            {
                Map<String, Object> currentThreadScope = threadScope.get();
                return () -> withThreadScope(currentThreadScope, task);
            }

            @Override
            public Runnable instrument(Runnable command)
            {
                Map<String, Object> currentThreadScope = threadScope.get();
                return () -> withThreadScope(currentThreadScope, command);
            }

            public void withThreadScope(Map<String, Object> newThreadScope, Runnable runnable)
            {
                try
                {
                    threadScope.set(newThreadScope);
                    runnable.run();
                }
                finally
                {
                    threadScope.remove();
                }
            }

            public <T> T withThreadScope(Map<String, Object> newThreadScope, Callable<T> callable) throws Exception
            {
                try
                {
                    threadScope.set(newThreadScope);
                    return callable.call();
                }
                finally
                {
                    threadScope.remove();
                }
            }
        };
    }
}

package micronaut.request.scope;

@ReactiveThreadLocal
public class MyReactiveThreadLocal
{

    private String appName;

    public MyReactiveThreadLocal()
    {
    }

    public MyReactiveThreadLocal(String appName)
    {
        this.appName = appName;
    }

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }
}

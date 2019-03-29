package micronaut.request.scope;

public class RequestMetadata
{

    private String appName;

    public RequestMetadata()
    {
    }

    public RequestMetadata(String appName)
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

package appu26j.utils.apps;

public class AppInfo
{
    private final String name, publisher, uninstallCmd, registryPath;

    public AppInfo(String name, String publisher, String uninstallCmd, String registryPath)
    {
        this.name = name;
        this.publisher = publisher;
        this.uninstallCmd = uninstallCmd;
        this.registryPath = registryPath;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPublisher()
    {
        return this.publisher;
    }

    public String getUninstallCmd()
    {
        return this.uninstallCmd;
    }

    public String getRegistryPath()
    {
        return this.registryPath;
    }
}

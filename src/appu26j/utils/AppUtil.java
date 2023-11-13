package appu26j.utils;

import appu26j.Registry;
import appu26j.gui.screens.GuiSoftuninstall;
import appu26j.utils.apps.AppInfo;

public class AppUtil
{
    public static void loadApps()
    {
        Thread thread1 = new Thread(() ->
        {
            try
            {
                loadAppsFromPath("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
            }

            catch (Exception e)
            {
                ;
            }

            GuiSoftuninstall.loading -= 1;
        });

        Thread thread2 = new Thread(() ->
        {
            try
            {
                loadAppsFromPath("HKEY_LOCAL_MACHINE\\SOFTWARE\\WoW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
            }

            catch (Exception e)
            {
                ;
            }

            GuiSoftuninstall.loading -= 1;
        });

        thread1.setDaemon(true);
        thread2.setDaemon(true);
        thread1.start();
        thread2.start();
    }

    private static void loadAppsFromPath(String pathToCheckAppsFrom) throws Exception
    {
        String[] paths = Registry.execute("QUERY \"" + pathToCheckAppsFrom + "\"").split("\n");

        for (String path : paths)
        {
            try
            {
                String keys = Registry.execute("QUERY \"" + path + "\"");
                boolean containsInfo = keys.contains("DisplayName ") && keys.contains("UninstallString ") && !keys.contains("SystemComponent ");

                if (!containsInfo)
                {
                    continue;
                }

                String name = Registry.execute("QUERY \"" + path + "\" /v DisplayName").trim().split(" {4}")[3];
                String publisher = keys.contains("Publisher") ? Registry.execute("QUERY \"" + path + "\" /v Publisher").trim().split(" {4}")[3] : "Unknown";
                String uninstallCmd = Registry.execute("QUERY \"" + path + "\" /v UninstallString").trim().split(" {4}")[3];
                GuiSoftuninstall.apps.add(new AppInfo(name, publisher, uninstallCmd, path));
            }

            catch (Exception e)
            {
                ;
            }
        }
    }
}

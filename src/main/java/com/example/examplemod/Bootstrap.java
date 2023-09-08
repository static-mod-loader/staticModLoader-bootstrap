package com.example.examplemod;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Paths;
import org.slf4j.Logger;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.io.*;
import java.net.URL;

public class Bootstrap {
    
    //v0.0.1a/libstatic_modloader__amd64-Linux.so
    static private String DLL_VER = "v0.0.1a";
    private String DLL_URL = "https://github.com/static-mod-loader/staticModLoader-object/releases/download/" + DLL_VER;
    
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public void downloadFile(String urlStr, String targetPath) throws IOException {
        URL url = new URL(urlStr);
        File targetFile = new File(targetPath);

        FileUtils.copyURLToFile(url, targetFile);
    }

    public int init() {
        LOGGER.info("[staticModloader] BOOTSTRAP INIT");
        LOGGER.info("[staticModloader] Working dir is: " + Paths.get("").toAbsolutePath().toString());

        LOGGER.info("[staticModloader] lib name == libstatic_modloader__" + System.getProperty("os.arch") + "-" + System.getProperty("os.name"));
        
        File modloaderDir = new File("./mods/staticModloader");
        if (modloaderDir.isDirectory() == false) {modloaderDir.mkdir();}

        File modloaderDLLPath = new File(".");
        String DLL_NAME = "";
        if (SystemUtils.IS_OS_LINUX) {
            modloaderDLLPath = new File("./mods/staticModloader/libstatic_modloader__" + System.getProperty("os.arch") + "-" + System.getProperty("os.name") + ".so");
            DLL_URL = DLL_URL + "/libstatic_modloader__" + System.getProperty("os.arch") + "-" + System.getProperty("os.name") + ".so";
            DLL_NAME = "libstatic_modloader__" + System.getProperty("os.arch") + "-" + System.getProperty("os.name") + ".so";
        } else {
            LOGGER.error("[staticModloader] OS not supported");
        }

        if (modloaderDLLPath.exists() == false) {
            try {
                downloadFile(DLL_URL, "./mods/staticModloader/" + DLL_NAME);
            } catch (IOException e) {
                LOGGER.error("[staticModloader] Download failed");
            }
        }
        
        System.load(modloaderDLLPath.getAbsolutePath());
        
        return 0;
    }
}

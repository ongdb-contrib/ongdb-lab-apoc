package data.lab.ongdb;

import data.lab.ongdb.util.FileUtils;
import data.lab.ongdb.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.OlabConfiguration
 * @Description: TODO
 * @date 2021/4/8 9:10
 */
public class OlabConfiguration {

    private static Map<String, Object> olabConfig = new HashMap<>(10);
    private static Map<String, Object> config = new HashMap<>(32);
    private static final Map<String, String> PARAM_WHITELIST = new HashMap<>(2);

    static {
        PARAM_WHITELIST.put("dbms.security.allow_csv_import_from_file_urls", "import.file.allow_read_from_filesystem");

        // Contains list of all dbms.directories.* settings supported by Neo4j.
        for(String directorySetting : FileUtils.NEO4J_DIRECTORY_CONFIGURATION_SETTING_NAMES) {
            PARAM_WHITELIST.put(directorySetting, directorySetting);
        }
    }

    public static Map<String, Object> get(String prefix) {
        return Util.subMap(olabConfig, prefix);
    }

    public static <T> T get(String key, T defaultValue) {
        return (T) olabConfig.getOrDefault(key, defaultValue);
    }

    public static Map<String,Object> list() {
        return config;
    }
}


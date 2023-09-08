import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class StaticLoaderProtocol {
    private static final Logger log = LogUtils.getLogger();

    public static void logInfo(String x) {
        log.info(x);
    }

}
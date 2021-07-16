import com.haier.autotest.server.SMTPServerHandler;
import com.haier.autotest.smtp.utils.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class FakeSMTP {
    private static final Logger log = LoggerFactory.getLogger(SMTPServerHandler.class);

    private FakeSMTP() {
        throw new UnsupportedOperationException();
    }

    public static void main(final String[] args) {
        try {
            SMTPServerHandler.INSTANCE.startServer(getPort(), getBindAddress());
        } catch (NumberFormatException e) {
            log.info("Error: Invalid port number", e);
        } catch (UnknownHostException e) {
            log.error("Error: Invalid bind address", e);
        } catch (Exception e) {
            log.error("Failed to auto-start server in background", e);
        }
    }

    /**
     * @return 默认端口8025或自定义端口（如果指定）。
     * @throws NumberFormatException if the specified port cannot be parsed to an integer.
     */
    private static int getPort() throws NumberFormatException {
        String portStr = Configuration.INSTANCE.get("smtp.default.port");
        return Integer.parseInt(StringUtils.isBlank(portStr) ? "8025" : portStr);
    }

    /**
     * @return 表示指定绑定地址的 InetAddress，如果未指定，则为 null
     * @throws UnknownHostException if the bind address is invalid
     */
    private static InetAddress getBindAddress() throws UnknownHostException {
        String bindAddressStr = Configuration.INSTANCE.get("smtp.default.bindAddress");
        if (bindAddressStr == null || bindAddressStr.isEmpty()) {
            return InetAddress.getLocalHost();
        }
        return InetAddress.getByName(bindAddressStr);
    }
}

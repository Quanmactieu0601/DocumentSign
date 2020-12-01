package vn.easyca.signserver.pki.sign.integrated.office;

import org.apache.commons.io.input.ProxyInputStream;

import java.io.IOException;
import java.io.InputStream;

public class NoCloseInputStream extends ProxyInputStream {

    public NoCloseInputStream(InputStream proxy) {
        super(proxy);
    }

    @Override
    public void close()
            throws IOException {
    }
}

package vn.easyca.signserver.pki.sign.integrated.office;

import org.apache.commons.io.output.ProxyOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class NoCloseOutputStream extends ProxyOutputStream {

    public NoCloseOutputStream(OutputStream proxy) {
        super(proxy);
    }

    @Override
    public void close()
            throws IOException {
    }
}

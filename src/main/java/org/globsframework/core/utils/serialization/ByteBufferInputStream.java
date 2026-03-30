package org.globsframework.core.utils.serialization;

import org.globsframework.core.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public final class ByteBufferInputStream extends InputStream {
    private final ByteBuffer byteBuffer;
    private final SeekableByteChannel channel;
    boolean eofReach = false;

    public ByteBufferInputStream(ByteBuffer byteBuffer, SeekableByteChannel channel) throws IOException {
        this.byteBuffer = byteBuffer;
        this.channel = channel;
        reload();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (eofReach) {
            return -1;
        }
        if (byteBuffer.hasRemaining()) {
            int read = Math.min(len, byteBuffer.remaining());
            byteBuffer.get(b, off, read);
            return read;
        }
        if (len == 0) {
            return 0;
        }
        byteBuffer.clear();
        reload();
        return read(b, off, len);
    }

    public int read() throws IOException {
        if (eofReach) {
            return -1;
        }
        if (byteBuffer.hasRemaining()) {
            return byteBuffer.get();
        }
        byteBuffer.clear();
        reload();
        if (eofReach) {
            return -1;
        }
        return byteBuffer.get();
    }

    private void reload() throws IOException {
        int read = channel.read(byteBuffer);
        while (read == 0) {
            read = channel.read(byteBuffer);
            Utils.sleep(100);
        }
        if (read == -1) {
            eofReach = true;
        } else {
            byteBuffer.flip();
        }
    }
}

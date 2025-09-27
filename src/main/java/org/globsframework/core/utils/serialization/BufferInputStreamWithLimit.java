package org.globsframework.core.utils.serialization;

import java.io.IOException;
import java.io.InputStream;

public class BufferInputStreamWithLimit extends InputStream {
    private final byte[] buffer;
    private int currentPos;
    private int count;
    private final InputStream inputStream;
    private int limit = Integer.MAX_VALUE;

    public BufferInputStreamWithLimit(InputStream inputStream) {
        this(inputStream, 8 * 1024);
    }

    public BufferInputStreamWithLimit(InputStream inputStream, int size) {
        this.inputStream = inputStream;
        buffer = new byte[size];
        currentPos = 0;
        count = 0;
    }

    public void resetLimit() {
        limit = Integer.MAX_VALUE;
    }

    public void limit(int size) {
        limit = currentPos + size;
    }

    public boolean readToLimit() throws IOException {
        if (limit != currentPos) {
            while (currentPos < limit) {
                if (read() == -1) {
                    limit = Integer.MAX_VALUE;
                    return false;
                }
            }
            limit = Integer.MAX_VALUE;
            return false;
        } else {
            limit = Integer.MAX_VALUE;
            return true;
        }
    }

    public int read() throws IOException {
        if (currentPos >= count) {
            count = inputStream.read(buffer);
            if (count == -1) {
                return -1;
            }
            if (limit != Integer.MAX_VALUE) {
                limit -= currentPos;
            }
            currentPos = 0;
        }
        if (currentPos >= limit) {
            throw new LimitReachedException(currentPos, limit, count);
        }
        return buffer[currentPos++] & 0xFF;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (currentPos + len > limit) {
            throw new LimitReachedException(currentPos, limit, count);
        }
        int available = count - currentPos;
        if (available > 0) {
            int length = Math.min(available, len);
            System.arraycopy(buffer, currentPos, b, off, length);
            currentPos += length;
            return length;
        }
        if (limit != Integer.MAX_VALUE) {
            limit = limit - currentPos;
        }
        currentPos = 0;
        if (len > buffer.length) {
            count = 0;
            final int read = inputStream.read(b, off, len);
            if (read == -1) {
                return -1;
            }
            currentPos = read;
            return read;
        }
        count = inputStream.read(buffer);
        if (count == -1) {
            return -1;
        }
        return read(b, off, len);
    }

    public void close() throws IOException {
        inputStream.close();
    }
}

package org.globsframework.core.utils.serialization;

import java.io.IOException;
import java.io.OutputStream;

public class ByteOutputInBytes implements DefaultSerializationOutput.ByteOutput {
    private final byte[] buffer = new byte[1024 * 1024];
    private OutputStream outputStream;
    private int position;

    public ByteOutputInBytes(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void writeOutputByte(int b)  {
        if (position == buffer.length) {
            try {
                outputStream.write(buffer, 0, buffer.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            position = 0;
        }
        buffer[position] = (byte) b;
        position++;
    }

    @Override
    public void writeOutputBytes(byte[] b, int len)  {
        if (position + len >= buffer.length) {
            try {
                outputStream.write(buffer, 0, position);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            position = 0;
            if (len > buffer.length) {
                throw new RuntimeException("overflow");
            }
        }
        System.arraycopy(b, 0, buffer, position, len);
        position += len;
    }

    public void flush(){
        if (position > 0) {
            try {
                outputStream.write(buffer, 0, position);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

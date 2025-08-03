package org.globsframework.core.utils.serialization;

import org.globsframework.core.metamodel.DummyModel;
import org.globsframework.core.model.Glob;
import org.globsframework.core.model.utils.GlobBuilder;
import org.globsframework.core.utils.TestUtils;
import org.junit.Test;

import static org.globsframework.core.metamodel.DummyObject.*;
import static org.junit.Assert.*;

public class EncoderTest {

    @Test
    public void testEncodesPrimitiveTypes() throws Exception {
        SerializedByteArrayOutput byteArrayOutput = new SerializedByteArrayOutput();
        SerializedOutput output = byteArrayOutput.getOutput();
        output.write(1);
        output.writeInteger(null);
        output.write(1L);
        output.writeLong(null);
        output.writeBytes(new byte[]{2, 2, 3, 4});
        output.writeBytes(null);
        output.writeDouble(1.3);
        output.writeDouble(null);
        output.writeByte(-12);
        output.writeByte(-1);

        SerializedInput input = byteArrayOutput.getInput();
        assertEquals(1, input.readNotNullInt());
        assertNull(input.readInteger());
        assertEquals(1L, input.readNotNullLong());
        assertNull(input.readLong());
        assertArrayEquals(new byte[]{2, 2, 3, 4}, input.readBytes());
        assertNull(input.readBytes());
        assertEquals(1.3, input.readDouble(), 0.001);
        assertNull(input.readDouble());
        assertEquals(-12, input.readByte());
        assertEquals(-1, input.readByte());
    }

    @Test
    public void testEncodesGlobs() throws Exception {

        int id = 1;
        int linkId = 33;
        String name = "aName";
        byte[] blob = TestUtils.SAMPLE_BYTE_ARRAY;
        boolean present = true;
        double value = 0.3;

        Glob glob = GlobBuilder
                .init(TYPE)
                .set(ID, id)
                .set(LINK_ID, linkId)
                .set(NAME, name)
                .set(PASSWORD, blob)
                .set(PRESENT, present)
                .set(VALUE, value).get();

        SerializedByteArrayOutput output = new SerializedByteArrayOutput();
        GlobSerializer globSerializer = new GlobSerializer(output.getOutput());

        globSerializer.writeGlob(glob);
        SerializedInput input = output.getInput();
        GlobDeSerializer globDeSerializer = new GlobDeSerializer(input);
        Glob decodedGlob = globDeSerializer.readGlob(DummyModel.get());

        assertEquals(1, decodedGlob.get(ID).intValue());
        assertEquals(linkId, decodedGlob.get(LINK_ID).intValue());
        assertEquals(name, decodedGlob.get(NAME));
        assertArrayEquals(blob, decodedGlob.get(PASSWORD));
        assertEquals(present, decodedGlob.get(PRESENT));
        assertEquals(value, decodedGlob.get(VALUE), 0.001);
    }
}

package bagaturchess.nnue_v7;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class LittleEndianDataInput implements Closeable {
    private static final String LEB128_MAGIC = "COMPRESSED_LEB128";

    private final InputStream in;

    LittleEndianDataInput(InputStream in) {
        this.in = new BufferedInputStream(in);
    }

    int readU8() throws IOException {
        int b = in.read();
        if (b < 0) {
            throw new EOFException();
        }
        return b;
    }

    byte readI8() throws IOException {
        return (byte) readU8();
    }

    int readI32() throws IOException {
        int a = readU8();
        int b = readU8();
        int c = readU8();
        int d = readU8();
        return a | (b << 8) | (c << 16) | (d << 24);
    }

    long readU32() throws IOException {
        return readI32() & 0xffffffffL;
    }

    String readString(int len) throws IOException {
        byte[] bytes = new byte[len];
        readFully(bytes);
        return new String(bytes, "UTF-8");
    }

    int peek() throws IOException {
        in.mark(1);
        int b = in.read();
        in.reset();
        return b;
    }

    void readFully(byte[] dst) throws IOException {
        int off = 0;
        while (off < dst.length) {
            int n = in.read(dst, off, dst.length - off);
            if (n < 0) {
                throw new EOFException();
            }
            off += n;
        }
    }

    int[] readLeb128IntArray(int count) throws IOException {
        byte[] magic = new byte[LEB128_MAGIC.length()];
        readFully(magic);
        String gotMagic = new String(magic, "US-ASCII");
        if (!LEB128_MAGIC.equals(gotMagic)) {
            throw new IOException("Missing COMPRESSED_LEB128 block");
        }

        int bytesLeft = readI32();
        int[] out = new int[count];
        for (int i = 0; i < count; i++) {
            int result = 0;
            int shift = 0;
            while (true) {
                if (bytesLeft-- <= 0) {
                    throw new EOFException("Corrupt LEB128 block");
                }
                int b = readU8();
                result |= (b & 0x7f) << (shift & 31);
                shift += 7;
                if ((b & 0x80) == 0) {
                    if (shift < 32 && (b & 0x40) != 0) {
                        result |= -1 << shift;
                    }
                    out[i] = result;
                    break;
                }
            }
        }

        while (bytesLeft-- > 0) {
            readU8();
        }
        return out;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}

package tk.amberide.engine.data.io;

import java.io.*;
import java.util.Arrays;

/**
 * A one-size-fit-all IO stream
 * 
 * @author Tudor
 */
public class ByteStream {
    protected byte[] buffer;
    protected volatile int _p = 0;

    public static ByteStream writeStream(int buffer) {
        ByteStream stream = new ByteStream();
        stream.buffer = new byte[buffer];//new DataOutputStream(stream.array = new ByteArrayOutputStream(buffer));
        return stream;
    }

    public static ByteStream writeStream() {
        return writeStream(0);
    }

    public static ByteStream writeStream(byte[] bytes) {
        ByteStream stream = new ByteStream();
        stream.buffer = bytes;
        return stream;
    }

    public static ByteStream readStream(byte[] bytes, int pos) {
        ByteStream stream = new ByteStream();
        stream._p = 0;
        stream.buffer = bytes;
        return stream;
    }

    public static ByteStream readStream(byte[] bytes) {
        return readStream(bytes, 0);
    }

    public static ByteStream readStream(File file) {
        try {
            return readStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("stream ended prematurely", e);
        }
    }

    public static ByteStream readStream(InputStream io) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[655653];
        int nRead;
        try {
            while ((nRead = io.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);
        } catch (IOException e) {
            throw new IllegalStateException("stream ended prematurely", e);
        }
        return readStream(buffer.toByteArray());
    }

    public ByteStream writeInt(final int i) {
        if (_p + 4 > buffer.length) {
            ensureCapacity(4);
        }
        byte[] data = buffer;
        data[_p++] = (byte) (i >>> 24);
        data[_p++] = (byte) (i >>> 16);
        data[_p++] = (byte) (i >>> 8);
        data[_p++] = (byte) i;
        return this;
    }

    public ByteStream writeLong(final long l) {
        writeInt((int) (l >>> 32));
        writeInt((int) l);
        return this;
    }

    public ByteStream writeDouble(double d) {
        return writeLong(Double.doubleToLongBits(d));
    }

    public final ByteStream writeUTF(String s) {
        int charLength = s.length();
        int len = _p;
        if (len + 2 + charLength > buffer.length) {
            ensureCapacity(charLength + 2);
        }

        byte[] data = buffer;
        buffer[len++] = (byte) (charLength >>> 8);
        data[len++] = (byte) charLength;

        _top:
        for (int i = 0; i < charLength; ++i) {
            char c;
            if ((c = s.charAt(i)) <= 0 || c > 127) {
                int byteLength = i;
                int j;
                for (j = i; j < charLength; ++j) {
                    if ((c = s.charAt(j)) > 0 && c <= 127) {
                        ++byteLength;
                    } else if (c > 2047) {
                        byteLength += 3;
                    } else {
                        byteLength += 2;
                    }
                }
                data[len++] = (byte) (byteLength >>> 8);
                data[len++] = (byte) byteLength;
                if (len + 2 + byteLength > data.length) {
                    ensureCapacity(byteLength + 2);
                    data = buffer;
                }
                j = i;
                while (true) {
                    j++;
                    if (j >= charLength) {
                        break _top;
                    }

                    if ((c = s.charAt(j)) > 0 && c <= 127) {
                        data[len++] = (byte) c;
                        continue;
                    } else if (c > 2047) {
                        data[len++] = (byte) (224 | c >> 12 & 15);
                        data[len++] = (byte) (128 | c >> 6 & 63);
                    } else {
                        data[len++] = (byte) (192 | c >> 6 & 31);
                    }
                    data[len++] = (byte) (128 | c & 63);
                }
            }
            data[len++] = (byte) c;
        }
        _p = len;
        return this;
    }


    public ByteStream writeShort(int s) {
        int length = _p;
        if (length + 2 > buffer.length) {
            ensureCapacity(2);
        }
        byte[] data = buffer;
        data[length++] = (byte) (s >>> 8);
        data[length++] = (byte) s;
        _p = length;
        return this;
    }

    public ByteStream writeByte(int b) {
        int length = _p;
        if (length + 1 > buffer.length) {
            ensureCapacity(1);
        }
        buffer[length++] = (byte) b;
        _p = length;
        return this;
    }

    public ByteStream writeBytes(byte[] b) {
        if (b.length > 0) {
            if (_p + b.length > buffer.length) {
                ensureCapacity(b.length);
            }
            System.arraycopy(b, 0, buffer, _p, b.length);
            _p += b.length;
        }
        return this;
    }

    public boolean readBoolean() {
        return readByte() == 1;
    }

    public int readUnsignedByte() {
        return buffer[_p++];
    }

    public short readShort() {
        byte[] b = buffer;
        return (short) (((b[_p++] & 0xFF) << 8) | (b[_p++] & 0xFF));
    }

    public int readUnsignedShort() {
        byte[] b = buffer;
        return ((b[_p++] & 0xFF) << 8) | (b[_p++] & 0xFF);
    }

    public int readInt() {
        byte[] b = buffer;
        return ((b[_p++] & 0xFF) << 24) | ((b[_p++] & 0xFF) << 16) | ((b[_p++] & 0xFF) << 8) | (b[_p++] & 0xFF);
    }

    public long readLong() {
        return (readInt() << 32) | readInt() & 0xFFFFFFFFL;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public byte readByte() {
        return (byte) (buffer[_p++] & 0xFF);
    }

    public String readUTF() {
        int size = readShort();
        int pos = 0;
        char[] buf = new char[size];
        int strLen = 0;

        while (pos < size) {
            int c;
            if ((c = buffer[_p + pos++] & 0xFF) < 0x80) {
                buf[strLen++] = (char) c;
            } else {
                char cc;
                if (c < 0xE0 && c > 0xBF) {
                    cc = (char) (c & 0x1F);
                } else {
                    cc = (char) ((c & 0xF) << 0x6 | c & 0x3F);
                }
                buf[strLen++] = (char) (cc << 0x6 | c & 0x3F);
            }
        }
        _p += pos;
        return new String(buf, 0, strLen);
    }

    public byte[] read(int n) {
        byte[] arr = new byte[n];
        System.arraycopy(buffer, _p, arr, 0, n);
        _p += n;
        return arr;
    }

    public byte[] getBuffer() {
        if (_p < buffer.length) {
            // This means that extra space has been allocated:
            // we cannot return this because it is essentially
            // corrupt. Hence, we truncate the end.
            byte[] newBuf = new byte[_p];
            System.arraycopy(buffer, 0, newBuf, 0, _p);
            // System.out.println("Reallocated " + this);
            // Set buffer to it so that we essentially return
            // a pointer.
            buffer = newBuf;
        }

        return buffer;
    }

    public void ensureCapacity(int size) {
        int mul = buffer.length << 1;
        int ad = _p + size;
        byte[] newData = new byte[mul > ad ? mul : ad];
        System.arraycopy(buffer, 0, newData, 0, _p);
        buffer = newData;
    }

    public int position() {
        return _p;
    }

    public void dump(File f) throws IOException {
        FileOutputStream fout = new FileOutputStream(f);
        fout.write(getBuffer());
        fout.close();
    }

    public String toString() {
        return "{ByteStream(" + buffer.length + "):" + Arrays.toString(buffer) + "}";
    }
}
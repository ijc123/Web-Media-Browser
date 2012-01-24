package servlet;

import java.io.IOException;
import java.io.OutputStream;

public class ChunkedOutputStream extends OutputStream {

    private static final byte[] CRLF = "\r\n".getBytes();
    private OutputStream output = null;

    public ChunkedOutputStream(OutputStream output) {
        this.output = output;
    }

    @Override
    public void write(int i) throws IOException {
        write(new byte[] { (byte) i }, 0, 1);
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        writeHeader(length);
        output.write(CRLF, 0, CRLF.length);
        output.write(b, offset, length);
        output.write(CRLF, 0, CRLF.length);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        writeHeader(0);
        output.write(CRLF, 0, CRLF.length);
        output.write(CRLF, 0, CRLF.length);
        output.close();
    }

    private void writeHeader(int length) throws IOException {
        byte[] header = Integer.toHexString(length).getBytes();
        output.write(header, 0, header.length);
    }

}


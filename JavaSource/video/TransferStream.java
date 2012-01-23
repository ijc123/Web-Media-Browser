package video;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class TransferStream extends RandomAccessFile {

	private OutputStream output;
	private byte[] buffer; 
	private int bufferSize;
	private int bufferPos;

	public TransferStream(OutputStream output, int bufferSize) throws FileNotFoundException {
		
		super("dummy", "rw");
		
		this.output = output;
		buffer = new byte[bufferSize];
		this.bufferSize = bufferSize;
		bufferPos = 0;
		
	}

	
	@Override
	public void close() throws IOException {
		
		//Returns the opaque file descriptor object associated with this stream.
		System.out.println("close()");
	}
	
	@Override
	public long	getFilePointer() {
		
		 //Returns the current offset in this file.
		System.out.println("long getFilePointer()");
		return 0;
		
	}
	
	@Override
	public long length() {
		
		 //Returns the length of this file.
		System.out.println("long length()");
		return 0;
	}

	@Override
	public int read() {
		
		 //Reads a byte of data from this file.
		System.out.println("read()");
		return 0;
	} 
	
	@Override
	public int read(byte[] b) {
		
		//Reads up to b.length bytes of data from this file into an array of bytes.
		System.out.println("int read(byte[] b)");
		return 0;
	}
	 
	@Override
	public int read(byte[] b, int off, int len) {
	
		 //Reads up to len bytes of data from this file into an array of bytes.
		System.out.println("int read(byte[] b, int off, int len)");
		return 0;
	}
	
	@Override
	public void seek(long pos) {
		
		 //Sets the file-pointer offset, measured from the beginning of this file, at which the next read or write occurs.
		System.out.println("void seek(" + Long.toString(pos) + ")");
	}
	
	@Override
	public void setLength(long newLength) {
		
		//Sets the length of this file.
		System.out.println("void setLength(long newLength)");
	}
	
	@Override
	public int skipBytes(int n) {
	
		System.out.println("int skipBytes(int n)");
		return 0;
	}
	
	@Override
	public void write(int val) throws IOException {
		System.out.println("write(int val)");
		//randomFile.write(val);
		//if(sync) { randomFile.getFD().sync(); }
	}

	@Override
	public void write(byte[] val) throws IOException {
		System.out.println("write(byte[] val)");
		//randomFile.write(val);
		//if(sync) { randomFile.getFD().sync(); }
	}

	@Override
	public void write(byte[] val, int off, int len) throws IOException {
		System.out.println("write(byte[] val," + Integer.toString(off) + "," + Integer.toString(len) + ")");
		
		writeToBuffer(val, off, len);
	
		//randomFile.write(val,off,len);
		//if(sync) { randomFile.getFD().sync(); }
	}
	
	private void writeToBuffer(byte[] val, int off, int len) throws IOException {
		
		if(bufferPos + len <= bufferSize) {
		
			System.arraycopy(val, off, buffer, bufferPos, len);
			bufferPos += len;
		
		} else {
			
			int bytesToWrite = bufferSize - bufferPos;
			System.arraycopy(val, off, buffer, bufferPos, bytesToWrite);
			
			output.write(buffer, 0, bufferSize);
			
			System.arraycopy(val, off + bytesToWrite, buffer, 0, len - bytesToWrite);
			
			bufferPos = len - bytesToWrite;
		}
	
	}

}

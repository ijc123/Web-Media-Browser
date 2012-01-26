package ftp;

import java.io.Closeable;
import java.io.IOException;

public interface VirtualInputFile extends Closeable {

	public void close() throws IOException; 	
	public long length() throws IOException;
	
	public int read() throws IOException; 	
	public int read(byte[] b) throws IOException;	 
	public int read(byte[] b, int off, int len) throws IOException;
	
	public void seek(long pos) throws IOException;
	
	public String getName();
	public long lastModified() throws IOException;
}

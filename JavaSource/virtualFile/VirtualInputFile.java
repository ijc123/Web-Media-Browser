package virtualFile;

import java.io.IOException;
import java.io.InputStream;

public abstract class VirtualInputFile extends InputStream  {

	public abstract void close() throws IOException; 	
	public abstract long length() throws IOException;
	
	public abstract int read() throws IOException; 	
	public abstract int read(byte[] b) throws IOException;	 
	public abstract int read(byte[] b, int off, int len) throws IOException;
	
	public abstract void seek(long pos) throws IOException;
	
	public abstract String getName();
	public abstract String getUri();
	public abstract long lastModified() throws IOException;
	

}

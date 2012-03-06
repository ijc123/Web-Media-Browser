package virtualFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LocalInputFile extends VirtualInputFile {

	private File info;
	private RandomAccessFile file;

	public LocalInputFile(String path) throws IOException {
			
		info = new File(path);
		
		file = new RandomAccessFile(info, "r");
		
	}
		
	public void close() throws IOException {
			
		file.close();
	}
	
	public long length() throws IOException {
		
		//Returns the length of this file.	
		return(file.length());
		
	}

	public int read() throws IOException {
		
		 //Reads a byte of data from this file.
		return(file.read());
	} 
	
	public int read(byte[] b) throws IOException {
		
		//Reads up to b.length bytes of data from this file into an array of bytes.
		return(file.read(b));
	}
	 
	public int read(byte[] b, int off, int len) throws IOException {
	
		 //Reads up to len bytes of data from this file into an array of bytes.
		return(file.read(b, off, len));
	}
	

	public void seek(long pos) throws IOException {
		
		 //Sets the file-pointer offset, measured from the beginning of this file, at which the next read or write occurs.		
		file.seek(pos);
	
	}

	@Override
	public String getName() {
		
		return(info.getName());
	}

	@Override
	public long lastModified() throws IOException {
		
		return(info.lastModified());
	}

	@Override
	public String getUri() {
		
		return(info.toURI().toString());
	}
}

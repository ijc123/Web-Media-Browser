package virtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Calendar;

public class RemoteInputFile implements VirtualInputFile {

	private InputStream input;
	private MyFTPClient ftp;
	
	public RemoteInputFile(String url) throws SocketException, IOException {
			
		ftp = new MyFTPClient(url);
		
		ftp.connect();
		
		input = null;
	}
	
	private void enableReading() throws IOException {
	
		if(input == null) {
			
			input = ftp.retrieveFileStream();
		}
	}
	
	private void disableReading() throws IOException {
		
		if(input != null) {
			
			input.close();
			
			if(!ftp.completePendingCommand()) {
				
				throw new IOException("error closing ftp input stream");
			}
			
			input = null;
		}
	}
	
	
	public void close() throws IOException {
		
		disableReading();
		ftp.disconnect();
	}
	
	public long length() throws IOException {
		
		disableReading();
		//Returns the length of this file.	
		return(ftp.getFileInfo().getSize());
		
	}

	public int read() throws IOException {
		
		enableReading();
		 //Reads a byte of data from this file.
		return(input.read());
	} 
	
	public int read(byte[] b) throws IOException {
		
		enableReading();
		//Reads up to b.length bytes of data from this file into an array of bytes.
		return(input.read(b));
	}
	 
	public int read(byte[] b, int off, int len) throws IOException {
	
		enableReading();
		 //Reads up to len bytes of data from this file into an array of bytes.
		return(input.read(b, off, len));
	}
	

	public void seek(long pos) throws IOException {
		
		disableReading();
		 //Sets the file-pointer offset, measured from the beginning of this file, at which the next read or write occurs.		
		ftp.setRestartOffset(pos);
	
	}


	@Override
	public String getName() {
		
		return ftp.getLocation().getFilename();
	}


	@Override
	public long lastModified() throws IOException {
		
		disableReading();
		Calendar modified = ftp.getFileInfo().getTimestamp();
				
		return modified.getTimeInMillis();
	}

	@Override
	public String getUri() {
				
		return(ftp.getLocation().getLocationWithoutUserInfo());
	}
	
/*
	public void setLength(long newLength) {
		
		//Sets the length of this file.
		System.out.println("void setLength(long newLength)");
	}
	

	public int skipBytes(int n) {
	
		System.out.println("int skipBytes(int n)");
		return 0;
	}
	

	public void write(int val) throws IOException {
		System.out.println("write(int val)");
	
	}


	public void write(byte[] val) throws IOException {
		System.out.println("write(byte[] val)");

	}


	public void write(byte[] val, int off, int len) throws IOException {
		System.out.println("write(byte[] val," + Integer.toString(off) + "," + Integer.toString(len) + ")");
		
	}
	
*/

}


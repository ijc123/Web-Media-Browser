package virtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

public class RemoteInputFile extends VirtualInputFile {

	private InputStream input;
	private MyFTPClient ftp;
	private FTPFile file;
	private Location location;
	
	public RemoteInputFile(Location location) throws SocketException, IOException {
		
		this.location = location;
				
		if(location.getFilename().isEmpty()) {
			
			throw new IOException("no remote input file specified");
		}
		
		URL url = URI.create(location.getURL()).toURL();
		
		ftp = new MyFTPClient(url);
		
		ftp.connect();
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		
		ftp.cwd(location.getPath());	
		
		FTPFile[] temp = ftp.listFiles(location.getFilename());	
		
		if(temp.length == 0) {
			
			throw new IOException("remote file does not exist: " + location.getLocation());
		}
		
		file = temp[0];
		
		input = null;
	}
	
	private void enableReading() throws IOException {
	
		if(input == null) {
																
			input = ftp.retrieveFileStream(location.getFilename());
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
		
			
		//Returns the length of this file.	
		return(file.getSize());
		
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
		
		return location.getFilename();
	}


	@Override
	public long lastModified() throws IOException {
		
		Calendar modified = file.getTimestamp();
				
		return modified.getTimeInMillis();
	}

	@Override
	public Location getLocation() {
		
		return(location);
	}



}


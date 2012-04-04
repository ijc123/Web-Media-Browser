package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import debug.Log;

import utils.MimeType;
import virtualFile.VirtualInputFile;

public abstract class LoadDataServlet extends HttpServlet {

    // Constants ----------------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;

	protected static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

	abstract protected VirtualInputFile getInputFile(HttpServletRequest request, 
    		HttpServletResponse response) throws IOException;
	
    // Properties ---------------------------------------------------------------------------------

    //private String imagePath;

    // Actions ------------------------------------------------------------------------------------

    public void init() throws ServletException {

        // Define base path somehow. You can define it as init-param of the servlet.
        //this.imagePath = "/images";

        // In a Windows environment with the Applicationserver running on the
        // c: volume, the above path is exactly the same as "c:\images".
        // In UNIX, it is just straightforward "/images".
        // If you have stored files in the WebContent of a WAR, for example in the
        // "/WEB-INF/images" folder, then you can retrieve the absolute path by:
        // this.imagePath = getServletContext().getRealPath("/WEB-INF/images");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
    
        // Decode the file name (might contain spaces and on) and prepare file object.
        VirtualInputFile data = getInputFile(request, response);
        if(data == null) return;
      
        // Get content type by filename.
        String contentType = MimeType.getMimeTypeFromExt(data.getName());

        // Check if file is actually an image (avoid download of other files by hackers!).
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        if (contentType == null) {
        	Log.error(this, "No mime type found for: " + data.getName());
            // Do your thing if the file appears not being a real image.
            // Throw an exception, or send 404, or show default/warning image, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Init servlet response.
        initServletResponse(response, data, contentType);
        writeOutputData(response, data);
      
    }

    protected void initServletResponse(HttpServletResponse response, VirtualInputFile data, String contentType) throws IOException {
    	
    	response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(data.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + data.getName() + "\"");	
    }
    
    protected void writeOutputData(HttpServletResponse response, VirtualInputFile data) throws IOException {
    	
    	  // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open streams.
            input = new BufferedInputStream(data, DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }
    }
    
    protected void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it.
                e.printStackTrace();
            }
        }
    }

}

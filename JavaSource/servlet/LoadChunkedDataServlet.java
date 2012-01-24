package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class LoadChunkedDataServlet extends LoadDataServlet {


	private static final long serialVersionUID = 1L;

	@Override
	protected void initServletResponse(HttpServletResponse response, File data, String contentType) {
    	
    	response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Transfer-Encoding","chunked");
      
    }
   
	@Override
    protected void writeOutputData(HttpServletResponse response, File data) throws IOException {
    	
    	  // Prepare streams.
        BufferedInputStream input = null;
        OutputStream output = null;

        try {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(data), DEFAULT_BUFFER_SIZE);
            output = new ChunkedOutputStream( new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE));

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

}

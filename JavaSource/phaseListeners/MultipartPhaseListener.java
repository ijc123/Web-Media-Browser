package phaseListeners;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class MultipartPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final boolean bDebug = false;

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
		//return PhaseId.ANY_PHASE;
	}

	public void afterPhase(PhaseEvent event) {

		if(bDebug) System.out.println("PhaseListener afterphase: " + event.getPhaseId());
			
	}

	@SuppressWarnings({ "unchecked" })
	public void beforePhase(PhaseEvent event) {

		Object requestObject = event.getFacesContext().getExternalContext().getRequest();
	
		if (!(requestObject instanceof HttpServletRequest)) {

			return;

		}

		HttpServletRequest httpServletRequest = (HttpServletRequest) requestObject;
		
		if(bDebug) System.out.println("PhaseListener beforephase: " +   httpServletRequest.getRequestURL() + " " +
				 httpServletRequest.getMethod() + " ");
			
				
		if (ServletFileUpload.isMultipartContent(httpServletRequest)) {

			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

			ServletFileUpload fileUpload = new ServletFileUpload(
					diskFileItemFactory);

			Map<String, String> formParamMap = new HashMap<String, String>();
			
			try {

				List<FileItem> parts = fileUpload
						.parseRequest(httpServletRequest);

				// first grab all the formfield data
				for (FileItem part : parts) {

					if (part.isFormField()) {
						
						String key = part.getFieldName();
				        String value = part.getString();
						
						formParamMap.put(key, value);
					}
				}
				
				// process the file
				for (FileItem part : parts) {

					if (!part.isFormField()) {

						processFileField(part, formParamMap);

					}
				}
			}

			catch (FileUploadException f) {

				f.printStackTrace();

			}
		}

	}

	private void processFileField(FileItem part, Map<String, String> formParamMap) {

		// Get filename prefix (actual name) and suffix (extension).
		//String filename = FilenameUtils.getName(part.getName());
		String filename = formParamMap.get("renameFile");
		String prefix = filename;
		String suffix = "";

		if (filename.contains(".")) {

			prefix = filename.substring(0, filename.lastIndexOf('.'));
			suffix = filename.substring(filename.lastIndexOf('.'));
		}

		InputStream input = null;
		OutputStream output = null;

		try {

			String path = formParamMap.get("fullPath");
			
			// Write uploaded file.
			File file = File.createTempFile(prefix + "_", suffix, new File(path));

			input = new BufferedInputStream(part.getInputStream(),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(new FileOutputStream(file),
					DEFAULT_BUFFER_SIZE);

			IOUtils.copy(input, output);

			// put(part.getFieldName(), file);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(input);
		}

		part.delete(); // Cleanup temporary storage.
	}

}

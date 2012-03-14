package utils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import virtualFile.FileInfo;
import virtualFile.FileUtils;
import virtualFile.FileUtilsFactory;
import virtualFile.Location;


public class ImagePreviewBuilder {

	private Comparator<FileInfo> FILENAME_ORDER =
			new Comparator<FileInfo>() {
			 
			 	@Override
			 	public int compare(FileInfo a, FileInfo b) {
			 		
			 		return a.getLocation().getLocation().compareTo(b.getLocation().getLocation());
			 	}
		 	};
	
	
	public void start(Location output) {
				
		try {
		
			output.setExtension("jpg");
			
			FileUtils source = FileUtilsFactory.create(output);
			
			ArrayList<FileInfo> thumbs = new ArrayList<FileInfo>();
			
			source.getDirectoryContents(thumbs, "*.png");
			
			Collections.sort(thumbs, FILENAME_ORDER);
			
			BufferedImage preview = null;
					  		
			for(int i = 0; i < thumbs.size(); i++) {
			
				FileInfo f = thumbs.get(i);
								
				File thumbImgFile = new File(f.getLocation().getLocation());
				
				BufferedImage thumbImg = ImageIO.read(thumbImgFile);
				
				int thumbWidth = thumbImg.getWidth();
				int thumbHeight = thumbImg.getHeight();
				
				if(preview == null) {
					
					int width = thumbWidth * 2;
					int height = thumbHeight * (thumbs.size() / 2);
										
					preview = new BufferedImage(width, height, thumbImg.getType());
									
				}
				
				Raster thumbRaster = (Raster)thumbImg.getRaster();
				
				WritableRaster previewRaster = preview.getRaster();
				
				int dx = (i % 2) * thumbWidth;
				int dy = (i / 2) * thumbHeight;
				
				previewRaster.setRect(dx, dy, thumbRaster);
				
				
			}
				  
			if(preview == null) return;
			
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = (ImageWriter)iter.next();

			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(1);

			FileOutputStream fileOutputStream = new FileOutputStream(output.getLocation());
			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(fileOutputStream);

			writer.setOutput(imageOutputStream);
			writer.write(null, new IIOImage(preview, null, null), iwp);			
		    
			imageOutputStream.close();
			fileOutputStream.close();
						
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}

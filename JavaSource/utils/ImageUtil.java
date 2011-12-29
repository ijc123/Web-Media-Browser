package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import database.ImageItem;

public class ImageUtil {
	
	public ImageItem createThumbNail(byte[] inputImageData, int thumbHeight) throws Exception 
	{

		InputStream inputStream = new ByteArrayInputStream(inputImageData);

		Image image = javax.imageio.ImageIO.read(inputStream);

		int imageWidth    = image.getWidth(null);
		int imageHeight   = image.getHeight(null);
		double imageRatio = (double)imageWidth / (double)imageHeight;

		int thumbWidth = (int)(thumbHeight * imageRatio);

		if(imageWidth < thumbWidth && imageHeight < thumbHeight) {

			thumbWidth = imageWidth;
			thumbHeight = imageHeight;

		} else if(imageWidth < thumbWidth) {

			thumbWidth = imageWidth;

		} else if(imageHeight < thumbHeight) {

			thumbHeight = imageHeight;
		}

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setBackground(Color.WHITE);
		graphics2D.setPaint(Color.WHITE); 
		graphics2D.fillRect(0, 0, thumbWidth, thumbHeight);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);


		// get jpeg writer and set highest quality
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = (ImageWriter)iter.next();

		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1);

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteOutputStream);

		writer.setOutput(imageOutputStream);
		writer.write(null, new IIOImage(thumbImage, null, null), iwp);

		//javax.imageio.ImageIO.write(thumbImage, "JPG", outputStream);

		ImageItem outputImage = new ImageItem();

		outputImage.setImageData(byteOutputStream.toByteArray());
		outputImage.setMimeType("image/jpeg");
		outputImage.setSizeBytes(byteOutputStream.size());

		imageOutputStream.close();
		byteOutputStream.close();
		inputStream.close();

		return(outputImage);
	}

}

/*
 * net/balusc/webapp/FileServlet.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package servlet;


/**
 * A file servlet supporting resume of downloads and client-side caching and GZIP of text content.
 * This servlet can also be used for images, client-side caching would become more efficient.
 * This servlet can also be used for text files, GZIP would decrease network bandwidth.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2009/02/fileservlet-supporting-resume-and.html
 */
public class VideoStreamingServlet extends LoadChunkedDataServlet {

    
	private static final long serialVersionUID = 1L;


}

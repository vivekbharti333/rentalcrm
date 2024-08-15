package com.datfusrental.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
//import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class GetBase64Image {

	/**
	 * Get Server Url
	 **/
	public String getServerPath(HttpServletRequest request) throws Exception {
		URL url = new URL(request.getRequestURL().toString());
		String serverPath = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
		return serverPath;
	}

	/** Upload Photos from base 64 **/
//	public String getPathToUploadFile(String Type) { // Use
//		String pathtoUploads;
//		if (Type.equalsIgnoreCase("USER")) {
//			pathtoUploads = Constant.docLocation + Constant.userPicPath;
//		} else if (Type.equalsIgnoreCase("Proof")) {
//			pathtoUploads = Constant.docLocation + Constant.proofPicPath;
//		} else if (Type.equalsIgnoreCase("VehicleImage")) {
//			pathtoUploads = Constant.docLocation + Constant.VehicleImage;
//		}else if (Type.equalsIgnoreCase("AadhharImage")) {
//			pathtoUploads = Constant.docLocation + Constant.AadhharImage;
//		}else if (Type.equalsIgnoreCase("RECIEPT")) {
//			pathtoUploads = Constant.docLocation + Constant.receipt;
//		}else if (Type.equalsIgnoreCase("VehicleNumberImage")) {
//			pathtoUploads = Constant.docLocation + Constant.VehicleNumberImage;
//		}
//		else {
//			pathtoUploads = Constant.docLocation + Constant.defaultPath;
//		}
//		if (!new File(pathtoUploads).exists()) {
//			File dir = new File(pathtoUploads);
//			dir.mkdirs();
//		}
//		Path path = Paths.get(pathtoUploads);
//		return path.toString();
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String uploadPhotos(String file, String fileName) {
		try {
			String fileBasefile = "";
			String[] values = file.split(",");
			ArrayList list = new ArrayList(Arrays.asList(values));
			if (list.size() >= 2) {
				fileBasefile = (String) list.get(1);
			} else {
				fileBasefile = fileName;
			}
			byte[] imageByteArray = Base64.decodeBase64(fileBasefile);
			FileOutputStream imageOutFile = new FileOutputStream(fileName);
			imageOutFile.write(imageByteArray);
			imageOutFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter writer;
			try {
				writer = new PrintWriter("/var/lib/tomcat8/webapps/test.txt", "UTF-8");
				writer.println(e.getMessage());
				writer.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return fileName;
	}

}

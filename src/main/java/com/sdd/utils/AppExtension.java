package com.sdd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URLDecoder;
import java.nio.file.FileSystems;

public class AppExtension {

	Logger logger = LogManager.getLogger(AppExtension.class);

	public String getFolderPath() {
		String baseUrl = "";
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
			String fullPath = URLDecoder.decode(path, "UTF-8");

			logger.debug("fullPath 1 -> {}", fullPath);
			if (fullPath.contains("WEB-INF")) {
				/* Live Enviornment (Tomcat) */
				String pathArr[] = fullPath.split("/WEB-INF/classes/");
				fullPath = pathArr[0];

				logger.debug("fullPath-> {}", fullPath);

				int endIndex = fullPath.lastIndexOf("/");
				String tomcatPath = fullPath.substring(0, endIndex);

				logger.debug("tomcatPath-> {}", tomcatPath);

				// to read a file from webApps
				baseUrl = new File(tomcatPath).getPath() + File.separatorChar;

				logger.debug("baseUrl-> {}", baseUrl);

				// String pathArr[] = fullPath.split("/WEB-INF/classes/");
				// fullPath = pathArr[0];
				// // to read a file from webcontent
				// baseUrl = new File(fullPath).getPath() + File.separatorChar;
			} else {
				/* Local Enviornment (IDE) */
				baseUrl = FileSystems.getDefault().getPath("src", "main", "webapp").toUri().toURL().toString();
				logger.debug("baseUrl-> {}", baseUrl);
//				baseUrl = "//home//ued//Documents//yatra-internet//yatra-web//src//main//webapp//";
				baseUrl = baseUrl.replaceAll("file:/", "");

			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return baseUrl;
	}
}

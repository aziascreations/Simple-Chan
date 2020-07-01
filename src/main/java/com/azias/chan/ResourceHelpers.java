package com.azias.chan;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ResourceHelpers {
	public static boolean isResourceAvailable(Path resourcePath) {
		return (ResourceHelpers.class.getResource(resourcePath.toString().replace('\\', '/')) != null);
	}
	
	public static InputStream getResource(Path resourcePath) {
		return ResourceHelpers.class.getResourceAsStream(resourcePath.toString().replace('\\', '/'));
	}
	
	public static String getResourceAsString(Path resourcePath) throws IOException {
		InputStream is = getResource(resourcePath);
		
		if(is == null) {
			throw new IOException("Resource not found: "+resourcePath.toString());
		}
		
		String content = new String(is.readAllBytes());
		is.close();
		
		return content;
	}
	
	public static ArrayList<Path> getResourceAsDirectoryListing(Path resourcePath) throws IOException {
		ArrayList<Path> paths = new ArrayList<>();
		
		String[] lines = getResourceAsString(resourcePath).split("\\r?\\n");
		
		for(int i=0; i<lines.length; i++) {
			paths.add(Paths.get(lines[i]));
		}
		
		return paths;
	}
}

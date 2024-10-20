package mg.itu.prom16.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JFile{
	String filename;
	InputStream filecontent;

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public InputStream getFilecontent() {
		return this.filecontent;
	}

	public void setFilecontent(InputStream filecontent) {
		this.filecontent = filecontent;
	}

	public void persistFile (String filePath)throws Exception{
		Path path = Paths.get(filePath+getFilename());
		Files.copy(getFilecontent(),path);
	}

}
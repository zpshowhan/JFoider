package org.zl.jf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletInputStream;

//A Class with methods used to process a ServletInputStream
public class HttpMultiPartParser {



	private final String lineSeparator = System.getProperty("line.separator", "\n");
	private final int ONE_MB = 1024 * 1;

	public Hashtable processData(ServletInputStream is, String boundary, String saveInDir,
			int clength) throws IllegalArgumentException, IOException {
		if (is == null) throw new IllegalArgumentException("InputStream");
		if (boundary == null || boundary.trim().length() < 1) throw new IllegalArgumentException(
				"\"" + boundary + "\" is an illegal boundary indicator");
		boundary = "--" + boundary;
		StringTokenizer stLine = null, stFields = null;
		FileInfo fileInfo = null;
		Hashtable dataTable = new Hashtable(5);
		String line = null, field = null, paramName = null;
		boolean saveFiles = (saveInDir != null && saveInDir.trim().length() > 0);
		boolean isFile = false;
		if (saveFiles) { // Create the required directory (including parent dirs)
			File f = new File(saveInDir);
			f.mkdirs();
		}
		line = getLine(is);
		if (line == null || !line.startsWith(boundary)) throw new IOException(
				"Boundary not found; boundary = " + boundary + ", line = " + line);
		while (line != null) {
			if (line == null || !line.startsWith(boundary)) return dataTable;
			line = getLine(is);
			if (line == null) return dataTable;
			stLine = new StringTokenizer(line, ";\r\n");
			if (stLine.countTokens() < 2) throw new IllegalArgumentException(
					"Bad data in second line");
			line = stLine.nextToken().toLowerCase();
			if (line.indexOf("form-data") < 0) throw new IllegalArgumentException(
					"Bad data in second line");
			stFields = new StringTokenizer(stLine.nextToken(), "=\"");
			if (stFields.countTokens() < 2) throw new IllegalArgumentException(
					"Bad data in second line");
			fileInfo = new FileInfo();
			stFields.nextToken();
			paramName = stFields.nextToken();
			isFile = false;
			if (stLine.hasMoreTokens()) {
				field = stLine.nextToken();
				stFields = new StringTokenizer(field, "=\"");
				if (stFields.countTokens() > 1) {
					if (stFields.nextToken().trim().equalsIgnoreCase("filename")) {
						fileInfo.name = paramName;
						String value = stFields.nextToken();
						if (value != null && value.trim().length() > 0) {
							fileInfo.clientFileName = value;
							isFile = true;
						}
						else {
							line = getLine(is); // Skip "Content-Type:" line
							line = getLine(is); // Skip blank line
							line = getLine(is); // Skip blank line
							line = getLine(is); // Position to boundary line
							continue;
						}
					}
				}
				else if (field.toLowerCase().indexOf("filename") >= 0) {
					line = getLine(is); // Skip "Content-Type:" line
					line = getLine(is); // Skip blank line
					line = getLine(is); // Skip blank line
					line = getLine(is); // Position to boundary line
					continue;
				}
			}
			boolean skipBlankLine = true;
			if (isFile) {
				line = getLine(is);
				if (line == null) return dataTable;
				if (line.trim().length() < 1) skipBlankLine = false;
				else {
					stLine = new StringTokenizer(line, ": ");
					if (stLine.countTokens() < 2) throw new IllegalArgumentException(
							"Bad data in third line");
					stLine.nextToken(); // Content-Type
					fileInfo.fileContentType = stLine.nextToken();
				}
			}
			if (skipBlankLine) {
				line = getLine(is);
				if (line == null) return dataTable;
			}
			if (!isFile) {
				line = getLine(is);
				if (line == null) return dataTable;
				dataTable.put(paramName, line);
				// If parameter is dir, change saveInDir to dir
				if (paramName.equals("dir")) saveInDir = line;
				line = getLine(is);
				continue;
			}
			try {
				UplInfo uplInfo = new UplInfo(clength);
				UploadMonitor.set(fileInfo.clientFileName, uplInfo);
				OutputStream os = null;
				String path = null;
				if (saveFiles) os = new FileOutputStream(path = getFileName(saveInDir,
						fileInfo.clientFileName));
				else os = new ByteArrayOutputStream(ONE_MB);
				boolean readingContent = true;
				byte previousLine[] = new byte[2 * ONE_MB];
				byte temp[] = null;
				byte currentLine[] = new byte[2 * ONE_MB];
				int read, read3;
				if ((read = is.readLine(previousLine, 0, previousLine.length)) == -1) {
					line = null;
					break;
				}
				while (readingContent) {
					if ((read3 = is.readLine(currentLine, 0, currentLine.length)) == -1) {
						line = null;
						uplInfo.aborted = true;
						break;
					}
					if (compareBoundary(boundary, currentLine)) {
						os.write(previousLine, 0, read - 2);
						line = new String(currentLine, 0, read3);
						break;
					}
					else {
						os.write(previousLine, 0, read);
						uplInfo.currSize += read;
						temp = currentLine;
						currentLine = previousLine;
						previousLine = temp;
						read = read3;
					}//end else
				}//end while
				os.flush();
				os.close();
				if (!saveFiles) {
					ByteArrayOutputStream baos = (ByteArrayOutputStream) os;
					fileInfo.setFileContents(baos.toByteArray());
				}
				else fileInfo.file = new File(path);
				dataTable.put(paramName, fileInfo);
				uplInfo.currSize = uplInfo.totalSize;
			}//end try
			catch (IOException e) {
				throw e;
			}
		}
		return dataTable;
	}

	/**
	 * Compares boundary string to byte array
	 */
	private boolean compareBoundary(String boundary, byte ba[]) {
		byte b;
		if (boundary == null || ba == null) return false;
		for (int i = 0; i < boundary.length(); i++)
			if ((byte) boundary.charAt(i) != ba[i]) return false;
		return true;
	}

	/** Convenience method to read HTTP header lines */
	private synchronized String getLine(ServletInputStream sis) throws IOException {
		byte b[] = new byte[1024];
		int read = sis.readLine(b, 0, b.length), index;
		String line = null;
		if (read != -1) {
			line = new String(b, 0, read);
			if ((index = line.indexOf('\n')) >= 0) line = line.substring(0, index - 1);
		}
		return line;
	}

	public String getFileName(String dir, String fileName) throws IllegalArgumentException {
		String path = null;
		if (dir == null || fileName == null) throw new IllegalArgumentException(
				"dir or fileName is null");
		int index = fileName.lastIndexOf('/');
		String name = null;
		if (index >= 0) name = fileName.substring(index + 1);
		else name = fileName;
		index = name.lastIndexOf('\\');
		if (index >= 0) fileName = name.substring(index + 1);
		path = dir + File.separator + fileName;
		if (File.separatorChar == '/') return path.replace('\\', File.separatorChar);
		else return path.replace('/', File.separatorChar);
	}

}

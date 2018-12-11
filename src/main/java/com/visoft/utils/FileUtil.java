package com.visoft.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.visoft.modules.docMng.model.AbstractDocFile;

/**
 * @author vlad
 *
 */
public final class FileUtil {

	private FileUtil() {
		// don't instantiate this class
	}

	/**
	 * @param outDir
	 * @param fileContent
	 * @param docFile
	 * @param projectId
	 * @throws IOException
	 */
	public static void writeFile2disc(final String outDir,
			final byte[] fileContent, AbstractDocFile docFile,
			final Optional<String> projectId) throws IOException {

		final String fileExt = FileUtil.getFileExtention(docFile.getFileName());
		final String outPathStr;
		if (projectId.isPresent()) {
			outPathStr = System.getProperty(Const.USER_DIR) + File.separator
					+ outDir + File.separator + projectId.get()
					+ File.separator;
		} else {
			outPathStr = System.getProperty(Const.USER_DIR) + File.separator
					+ outDir + File.separator;
		}

		new File(outPathStr).mkdirs();
		final String _id = docFile.getId().toString();
		final Path out = Paths.get(outPathStr + _id + "." + fileExt);
		Files.write(out, fileContent);
	}
	
	/**
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String getFileType(final String fileName) throws IOException {
		String fileType = "Undetermined";
		final File file = new File(fileName);
		fileType = Files.probeContentType(file.toPath());

		return fileType;
	}
	
	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStream2bytes(final InputStream is) 
	  throws IOException {
	   	 
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[1024];
	    while ((nRead = is.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }
	 
	    buffer.flush();
	    return buffer.toByteArray();
	}
	
	/**
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(final InputStream input)
			throws IOException {
		try (BufferedReader buffer = new BufferedReader(
				new InputStreamReader(input, "UTF-8"))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	/**
	 * @param bFile
	 * @param fileName
	 * @throws IOException
	 */
	public static void writeBytesToFileNio(final byte[] bFile,
			final String fileName) throws IOException {

		Path path = Paths.get(fileName);
		Files.write(path, bFile);

	}

	/**
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public static List<Path> getRecursiveFileList(final String folderName)
			throws IOException {
		Path srcDir = Paths.get(folderName);

		return Files.walk(srcDir).filter(path -> Files.isRegularFile(path))
				.map(Path::getFileName).collect(Collectors.toList());
	}

	public static String getFileExtention(String fileName) {
		String[] split = fileName.split("\\.");
	    String ext = split[split.length - 1];
	    return ext;
	}
}

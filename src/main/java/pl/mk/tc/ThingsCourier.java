package pl.mk.tc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.mk.tc.util.EProperties;
import pl.mk.utility.PropertiesReader;

public class ThingsCourier {

	private final Properties properties;

	private Logger log = LogManager.getLogger(ThingsCourier.class);

	public ThingsCourier(Properties properties) {
		super();
		this.properties = properties;
	}

	public void run() {

		FTPClient ftpClient = new FTPClient();
		try {

			String server = properties.getProperty(EProperties.READ_FTP_SERVER.name);
			int port = Integer.parseInt(properties.getProperty(EProperties.READ_FTP_PORT.name));
			String user = properties.getProperty(EProperties.READ_FTP_USER.name);
			String pass = properties.getProperty(EProperties.READ_FTP_PASSWORD.name);

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			String readFilesSystemSeparator = properties.getProperty(EProperties.READ_FTP_SEPARATOR.name);
			String dir = properties.getProperty(EProperties.READ_DIR.name);
			String saveDir = properties.getProperty(EProperties.SAVE_DIR.name);
			List<String> filenames = PropertiesReader.getMultiProperty(properties, EProperties.READ_FILENAMES.name);

			if (!createDirIfNotExists(saveDir)) {
				this.log.error("Error occurred! aborting ThingsCourier");
				return;
			}

			for (String filename : filenames) {
				synchFile(ftpClient, dir, saveDir, filename, readFilesSystemSeparator);
			}
		} catch (Exception e) {
			log.error("Error while running ThingsCourier", e);
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException e) {
				log.error("Error while disconnecting ThingsCourier", e);
			}
		}
	}

	private boolean createDirIfNotExists(String saveDir) {
		File saveDirFile = new File(saveDir);
		if (!saveDirFile.exists()) {
			this.log.info("Save directory was not exists! creating...");
			if (!saveDirFile.mkdirs()) {
				this.log.error("Error while creating directory for files.");
				return false;
			}
		}
		this.log.info("Save directory exists!");
		return true;
	}

	private void synchFile(FTPClient ftpClient, String dir, String saveDir, String filename,
			String readFilesSystemSeparator) {
		String filepath = dir.concat(readFilesSystemSeparator).concat(filename);
		this.log.info("Read : {}", filepath);
		File savefile = new File(saveDir.concat(FileSystems.getDefault().getSeparator()).concat(filename));
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(savefile))) {
			boolean success = ftpClient.retrieveFile(filepath, outputStream);
			if (success)
				this.log.info("Saved: {}", savefile.getAbsolutePath());
			else
				this.log.error("Some error occurred while moving file");
		} catch (Exception e1) {
			this.log.error("Error while downloading file", e1);
		}
	}
}
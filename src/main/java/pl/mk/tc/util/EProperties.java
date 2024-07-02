package pl.mk.tc.util;

public enum EProperties {

	SAVE_DIR("save_dir"), READ_DIR("read_dir"), READ_URL("read_ftp_url"), READ_FILENAMES("read_filenames"),
	SERVER_FILE_DATE_MODIFICATION_READ("server_file_date_modification_read"), ADDITIONAL_LOGS("additional_logs"),
	READ_FTP_PASSWORD("read_ftp_password"), READ_FTP_USER("read_ftp_user"), READ_FTP_PORT("read_ftp_port"),
	READ_FTP_SERVER("read_ftp_server"), READ_FTP_SEPARATOR("READ_FTP_SEPARATOR");

	public String name;

	private EProperties(final String name) {
		this.name = name;
	}
}

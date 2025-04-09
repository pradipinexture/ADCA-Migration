package adc.dxp.rest.api.application.data;

import java.util.Date;

import com.liferay.document.library.kernel.model.DLFileEntry;

import adc.dxp.rest.api.application.utils.ContentType;

public class Document extends Content {
	
	private String version;
	private String uuid;
	private long folderId;
	private long entryId;
	private String groupId;
	private String mimeType;
	private String filename;
	private String filenameUrl;
	private String detailUrl;
	private String imageUrl;
	private String downloadUrl;
	private String instanceId;
	private Date date;	
	private String fileType;	
	
	public Document(DLFileEntry file, String groupId) {
		super(ContentType.DOCUMENT);
		this.version = file.getVersion();
		this.uuid = file.getUuid();
		this.folderId = file.getFolderId();
		this.entryId = file.getFileEntryId();
		this.groupId = groupId;
		this.mimeType = file.getMimeType();
		
		if (mimeType.contains("video")) {
			fileType = "video";
		}		
		else if (mimeType.contains("image")) {
			fileType = "image";
		}
		else if (mimeType.contains("pdf")) {
			fileType = "pdf";
		}
		else {
			fileType = "document";
		}
			
		
			
		this.filename = file.getFileName();
		this.date = file.getModifiedDate();		
		filenameUrl = filename.replace(' ', '+');		
		imageUrl = "/documents/" + groupId + "/" + folderId + "/" + filenameUrl + "/" + uuid + "?version=" + version + "&documentThumbnail=1";
		downloadUrl = "/documents/" + groupId + "/" + folderId + "/" + filenameUrl + "/" + uuid + "?download=true";
	}

	/*public Document(DLFileEntry file, String groupId, String instanceId) {
		this(file, groupId);
		detailUrl = "/group/portal/corporate/-/document_library/" + instanceId + "/view_file/" + entryId;
	}*/
	

	// getters + setters
	
	public String getUrlImage() {
		return imageUrl;
	}

	public String getUrlDetail() {
		return detailUrl;
	}
		
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getFolderId() {
		return folderId;
	}

	public void setFolderId(long folderId) {
		this.folderId = folderId;
	}

	public long getEntryId() {
		return entryId;
	}

	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilenameUrl() {
		return filenameUrl;
	}

	public void setFilenameUrl(String filenameUrl) {
		this.filenameUrl = filenameUrl;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}	

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}

package adc.dxp.rest.api.application.data.vo;

import java.util.List;

import adc.dxp.rest.api.application.data.Media;

/**
 * 
 * @author ricardo.gomes
 *
 */
public class GalleryVO {

	Media media;

	List<String> srcs;

	public GalleryVO(Media media, List<String> srcs) {
		super();
		this.media = media;
		this.srcs = srcs;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public List<String> getSrcs() {
		return srcs;
	}

	public void setSrcs(List<String> srcs) {
		this.srcs = srcs;
	}

}

package gov.nih.nlm.ceb.lpf.imagestats.shared;


import java.io.Serializable;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class IplImageStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String imageUrl = "";
	int nChannels, depth, width,height, dataOrder,origin,widthStep,	imageSize, align;
	String colorModel = "";
	public IplImageStats(String url, IplImage iplImage) {
		
		imageUrl = url;
		if(iplImage != null) {
			nChannels = iplImage.nChannels();
			depth = iplImage.depth();
			width = iplImage.width();
			height = iplImage.height();
			dataOrder = iplImage.dataOrder();
			origin = iplImage.origin();
			widthStep = iplImage.widthStep();
			imageSize = iplImage.imageSize();
			align = iplImage.align();
			colorModel = Utils.getColorModel(iplImage);
		}
		
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public int getNChannels() {
		return nChannels;
	}
	public int getDepth() {
		return depth;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getDataOrder() {
		return dataOrder;
	}
	public int getOrigin() {
		return origin;
	}
	public int getWidthStep() {
		return widthStep;
	}
	public int getImageSize() {
		return imageSize;
	}
	public int getAlign() {
		return align;
	}
	public String getColorModel() {
		return colorModel;
	}

	public void setImageUrl(String val) {
		imageUrl = val;
	}
	
	public void setNChannels(int val) {
		nChannels = val;
	}
	public void setDepth(int val) {
		depth = val;
	}
	public void setWidth(int val) {
		width = val;
	}
	public void setHeight(int val) {
		height = val;
	}
	public void setDataOrder(int val) {
		dataOrder = val;
	}
	public void setOrigin(int val) {
		origin = val;
	}
	public void setWidthStep(int val) {
		widthStep = val;
	}
	public void setImageSize(int val) {
		imageSize = val;
	}
	public void setAlign(int val) {
		align = val;
	}
	public void setColorModel(String val) {
		colorModel = val;
	}

}

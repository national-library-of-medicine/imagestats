package gov.nih.nlm.ceb.lpf.imagestats.shared;

import java.math.BigDecimal;

import gov.nih.nlm.ceb.lpf.imagestats.client.ImageEditContainer;

import org.vaadin.gwtgraphics.client.DrawingArea;

public class ImageDetails extends PLRecord {
	DrawingArea canvas;
	int itemNum = 0;
	public ImageDetails() {
		super();
	}
	
	public ImageDetails(PLRecord other, int itemIndex) {
		super(other);
		itemNum = itemIndex;
	}
	
	public void setCanvas(DrawingArea canvas) {
		this.canvas = canvas;
	}
	
	public String getAnnotationsFromCanvas() {
		BigDecimal nFactor = ImageEditContainer.getNormalizationFactor(getImageWidth(), getImageHeight());
		ImageRegionModel[] regions = ImageEditContainer.getModelsFromCanvas(nFactor, canvas);
		return ClientUtils.arrayToString(regions);
	}

	public int getItemIndex() {
		return itemNum;
	}

}

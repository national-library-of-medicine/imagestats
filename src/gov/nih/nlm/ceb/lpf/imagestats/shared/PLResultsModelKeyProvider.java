package gov.nih.nlm.ceb.lpf.imagestats.shared;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class PLResultsModelKeyProvider implements ModelKeyProvider<FacetModel> {

	@Override
	public String getKey(FacetModel item) {
		return item.getField();
	}
}

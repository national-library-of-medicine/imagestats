package gov.nih.nlm.ceb.lpf.imagestats.shared;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PLEventProperties extends PropertyAccess<PLEvent> {
  ValueProvider<PLRecord, String> displayName();
 
  ModelKeyProvider<PLRecord> shortname();

}

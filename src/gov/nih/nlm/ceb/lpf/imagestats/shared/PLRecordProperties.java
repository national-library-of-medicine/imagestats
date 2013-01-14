package gov.nih.nlm.ceb.lpf.imagestats.shared;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PLRecordProperties extends PropertyAccess<PLRecord> {
  ValueProvider<PLRecord, String> name();
 
  ModelKeyProvider<PLRecord> id();
 
  ValueProvider<PLRecord, String> uuid();
 
  ValueProvider<PLRecord, String> imageUrl();
  ValueProvider<PLRecord, String> url();
  ValueProvider<PLRecord, String> url_thumb();
  ValueProvider<PLRecord, String> eventName();
  ValueProvider<PLRecord, String> originalUrl();
  /*
  ValueProvider<PLRecord, String> colorChannels();
  ValueProvider<PLRecord, String> imageWidth();
  ValueProvider<PLRecord, String> imageHeigth();
  */
}

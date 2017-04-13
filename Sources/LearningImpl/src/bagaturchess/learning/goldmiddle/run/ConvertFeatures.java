package bagaturchess.learning.goldmiddle.run;



import java.util.HashMap;
import java.util.Map;



import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.goldmiddle.impl.cfg.old0.FeaturesConfigurationBagaturImpl;
import bagaturchess.learning.impl.features.baseimpl.Features;


public class ConvertFeatures {
	
	
	public static void main(String[] args) throws Exception {
		Features pfs = Features.load();
		Map<String, IFeature> byname_p = createByNameMap(pfs);
		Map<String, IFeature> byname_m = createByNameMap(Features.createNewFeatures(FeaturesConfigurationBagaturImpl.class.getName()));
		
		for (String key: byname_p.keySet()) {
			IFeature val_p = byname_p.get(key);
			IFeature val_m = byname_m.get(key);
			if (val_m != null) {
				//val_p.setID(val_m.getId());
			} else {
				throw new IllegalStateException("val_m is null");
			}
		}
		
		//Features converted = new Features(pfs.getFeatures());
		
		pfs.store();
		
		
		Features.dump(Features.load().getFeatures());
	}
	
	
	private static Map<String, IFeature> createByNameMap(Features fs) {
		IFeature[] fs_arr = fs.getFeatures();
		Map<String, IFeature> byname = new HashMap<String, IFeature>();
		for (int i=0; i<fs_arr.length; i++) {
			IFeature f = fs_arr[i];
			byname.put(f.getName(), f);
		}
		return byname;
	}
}

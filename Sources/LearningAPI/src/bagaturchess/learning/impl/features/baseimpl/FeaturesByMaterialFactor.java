package bagaturchess.learning.impl.features.baseimpl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeaturesConfiguration;


public class FeaturesByMaterialFactor {
	
	
	public static final String FEATURES_FILE_NAME = "./features.by.material.factor.bin";
	
	
	private static final String FEATURES_FILE_NAME_BACKUP_SUFFIX = ".backup";
	
	private Map<Integer, IFeature[]> features_by_material_factor;
	
	
	private FeaturesByMaterialFactor(Map<Integer, IFeature[]> _features_by_material_factor) {
		
		features_by_material_factor = _features_by_material_factor;
	}
	
	
	public Map<Integer, IFeature[]> getFeaturesForEachMaterialFactor() {
		return features_by_material_factor;
	}
	
	
	public static FeaturesByMaterialFactor load(String fileName, String cfgClassName) throws Exception {
		
		Map<Integer, IFeature[]> features_by_material_factor = null;
		
		try {
			
			File org = new File(fileName);
			
			if (!org.exists()) {
				
				System.out.println("FeaturesPersistency.load: org not found");
				
				File backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
				
				if (!backup.exists()) {
					
					System.out.println("FeaturesPersistency.load: backup not found");
					System.out.println("FILE NOT FOUND: " + fileName + ", it will be created later ...");
					
				} else {
					
					System.out.print("FeaturesPersistency.load: rename backup to org - ");
					boolean ok = backup.renameTo(org);
					System.out.println("" + ok);
				}
			}
			
			InputStream is = new FileInputStream(fileName);
			
			ObjectInputStream ois = new ObjectInputStream(is);
			
			features_by_material_factor = (Map<Integer, IFeature[]>) ois.readObject();
			
			ois.close();
			
			is.close();
			
		} catch (Exception e) {
			
			//e.printStackTrace();
			
			features_by_material_factor = new HashMap<Integer, IFeature[]>();
			
			for (int total_material_factor = 0; total_material_factor < 64; total_material_factor++) {
				
				features_by_material_factor.put(total_material_factor, createNewFeatures(cfgClassName));
			}
		}
		
		
		return new FeaturesByMaterialFactor(features_by_material_factor);
	}
	
	
	private static IFeature[] createNewFeatures(String cfgClassName) throws Exception {
		
		IFeaturesConfiguration fc = (IFeaturesConfiguration) FeaturesByMaterialFactor.class.getClassLoader().loadClass(cfgClassName).newInstance();
		
		return fc.getDefinedFeatures();
	}
	
	
	public static void store(String fileName, Map<Integer, IFeature[]> features_by_material_factor) {
		
		try {
			
			
			//Delete backup
			File backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
			if (backup.exists()) {
				boolean ok = backup.delete();
			}
			
			
			//Rename the org to backup
			File org = new File(fileName);
			backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
			boolean ok = org.renameTo(backup);
			
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(features_by_material_factor);
			oos.flush();
			oos.close();
			
			
			boolean ok1 = backup.delete();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	public String toString() {
		String result = "toString not implemented ...";
		/*for (IFeature f: features_all.values()) {
			result += f + ", " + "\r\n";
		}*/
		return result;
	}
	

	public static void dump(IFeature[] fs) {
		
		for (int i=0; i<fs.length; i++) {
			System.out.println(fs[i] + ", ");
		}
	}


	public static void toJavaCode(IFeature[] features) {
		for (int i = 0; i < features.length; i++) {
			System.out.println(features[i].toJavaCode());
		}
	}
}

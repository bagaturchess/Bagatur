package bagaturchess.learning.impl.features.baseimpl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeaturesConfiguration;


public class Features_Splitter {
	
	
	public static final String FEATURES_FILE_NAME = "goldenmiddle.mf.bin";
	
	
	private static final String FEATURES_FILE_NAME_BACKUP_SUFFIX = ".backup";
	
	private Map<Integer, IFeature[]> features_by_material_factor;
	
	private List<IFeature[]> features_by_material_factor_2uniouns;
	
	
	public IFeature[] getFeatures(IBitBoard board) {
		
		/*int total_factor = Math.min(63, board.getMaterialFactor().getTotalFactor());
		
		int material_factor_phase = total_factor / 16;
		
		int castling_pair = board.getCastlingPair().ordinal();
		
		return features_by_material_factor_2uniouns.get(4 * castling_pair + material_factor_phase);*/
		
		int total_factor = Math.min(63, board.getMaterialFactor().getTotalFactor());
		
		return features_by_material_factor_2uniouns.get(total_factor / 16);
	}
	
	
	public static Features_Splitter load(String fileName, String cfgClassName) throws Exception {
		
		List<IFeature[]> features_by_material_factor_2uniouns = null;
			
		File org = new File(fileName);
		
		if (!org.exists()) {
			
			System.out.println("FeaturesPersistency.load: org not found");
			
			File backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
			
			if (!backup.exists()) {
				
				System.out.println("FeaturesPersistency.load: backup not found");
				//System.out.println("FILE NOT FOUND: " + fileName + ", it will be created later ...");
				
			} else {
				
				System.out.print("FeaturesPersistency.load: rename backup to org - ");
				boolean ok = backup.renameTo(org);
				System.out.println("" + ok);
			}
		}
		
		InputStream is = new FileInputStream(fileName);
		
		ObjectInputStream ois = new ObjectInputStream(is);
		
		features_by_material_factor_2uniouns = (List<IFeature[]>) ois.readObject();
		
		ois.close();
		
		is.close();
		
		return new Features_Splitter(features_by_material_factor_2uniouns);
	}
	
	
	public static Features_Splitter create(String cfgClassName) throws Exception {
		
		List<IFeature[]> features = new ArrayList<IFeature[]>();
		
		features.add(createNewFeatures(cfgClassName));
		features.add(createNewFeatures(cfgClassName));
		features.add(createNewFeatures(cfgClassName));
		features.add(createNewFeatures(cfgClassName));
		
		/*for (int castling_pair = 0; castling_pair < 9; castling_pair++) {
		
			for (int material_factor_phase = 0; material_factor_phase < 4; material_factor_phase++) {
				
				features.add(createNewFeatures(cfgClassName));
			}
		}*/
		
		return new Features_Splitter(features);
	}
	
	
	
	private Features_Splitter(List<IFeature[]> _features_by_material_factor_2uniouns) {
		
		features_by_material_factor_2uniouns = _features_by_material_factor_2uniouns;
	}
	
	
	private static Features_Splitter load_features_by_material_factor(String fileName, String cfgClassName) throws Exception {
		
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
		
		
		return new Features_Splitter(features_by_material_factor);
	}
	
	
	private Features_Splitter(Map<Integer, IFeature[]> _features_by_material_factor) {
		
		features_by_material_factor = _features_by_material_factor;
	}
	
	
	private static IFeature[] createNewFeatures(String cfgClassName) throws Exception {
		
		IFeaturesConfiguration fc = (IFeaturesConfiguration) Features_Splitter.class.getClassLoader().loadClass(cfgClassName).newInstance();
		
		return fc.getDefinedFeatures();
	}
	
	
	public static void updateWeights(Features_Splitter splitter, boolean clear_history) {
		
		for (IFeature[] features: splitter.features_by_material_factor_2uniouns) {
			
			for (IFeature feature: features) {
				
				if (feature != null) {
					
					((IAdjustableFeature) feature).applyChanges();
					
					if (clear_history) ((IAdjustableFeature) feature).clear();
				}
			}
		}
	}
	
	
	public static void store(String fileName, Features_Splitter splitter) {
		
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
			
			//oos.writeObject(splitter.features_by_material_factor);
			oos.writeObject(splitter.features_by_material_factor_2uniouns);
			
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
	
	
	public static void dump(Features_Splitter splitter) {
		
		for (IFeature[] features: splitter.features_by_material_factor_2uniouns) {
			
			for (IFeature feature: features) {
					
				System.out.println(feature + ", ");
			}
		}
	}


	public static void toJavaCode(IFeature[] features) {
		for (int i = 0; i < features.length; i++) {
			System.out.println(features[i].toJavaCode());
		}
	}
}



package bagaturchess.learning.impl.features.baseimpl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_NONE;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_I2;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_I1;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_I2;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_I3;
//import bagaturchess.learning.cfg.bagatur.FeaturesConfigurationBagaturImpl_I1;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.IFeaturesConfiguration;
import bagaturchess.learning.api.IFeaturesMerger;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.signals.Signals;


public class Features {
	
	
	private static final String FEATURES_FILE_NAME = "./features.bin";
	private static final String FEATURES_FILE_NAME_BACKUP_SUFFIX = ".backup";
	
	private Map<Integer, IFeature> features_all;
	private Map<Integer, IFeature>[] features_by_complexity;
	
	
	private Features(IFeature[] all) {
		
		features_all = new TreeMap<Integer, IFeature>();
		
		for (int i=0; i<all.length; i++) {
			IFeature f = all[i];
			if (features_all.containsKey(f.getId())) {
				throw new IllegalStateException();
			}
			features_all.put(f.getId(), f);
		}
		//features_all = all;
		
		/*for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.setAdjustable(true);
		}*/
		
		checkConsistency();
		
		features_by_complexity = new Map[IFeatureComplexity.MAX];//new IFeature[IFeatureComplexity.MAX][];
		for (int i=0; i<IFeatureComplexity.MAX; i++) {
			Map<Integer, IFeature> byComplexity = new HashMap<Integer, IFeature>();	
			for (Integer f_key: features_all.keySet()) {
				IFeature f = features_all.get(f_key);
				if (f.getComplexity() == i) {
					byComplexity.put(f.getId(), f);
				}
			}
			features_by_complexity[i] = byComplexity;
		}
		
		/*List<IFeature> pst = new ArrayList<IFeature>();	
		for (IFeature f: features_all) {
			if (f instanceof FeaturePST) {
				pst.add(f);
			}
		}
		
		features_pst = pst.toArray(new IFeature[0]);*/ 
	}
	
	
	public static Features createNewFeatures(String cfgClassName) throws Exception {
		IFeaturesConfiguration fc = (IFeaturesConfiguration) Features.class.getClassLoader().loadClass(cfgClassName).newInstance();
		IFeature[] fs = fc.getDefinedFeatures();
		
		/*for (int i=0; i<fs.length; i++) {
			fs[i].setAdjustable(false);
		}*/
		
		Features f = new Features(fs);
		return f;
	}
	
	public ISignals createSignals() {
		return new Signals(getFeatures());
	}
	
	
	public IFeature[] getFeatures() {
		return features_all.values().toArray(new IFeature[0]);
	}
	
	public IFeature get(int id) {
		return features_all.get(id);
	}
	
	public IFeature[] getAllByComplexity(int complexity) {
		return features_by_complexity[complexity].keySet().toArray(new IFeature[0]);
	}
	
	public IFeature[][] getAllByComplexity() {
		int size = features_by_complexity.length;
		IFeature[][] arr = new IFeature[size][];
		for (int comp=0; comp<features_by_complexity.length; comp++) {
			arr[comp] = features_by_complexity[comp].values().toArray(new IFeature[0]);
		}
		return arr;
	}
	
	
	private void checkConsistency() {
		/*for (int i=0; i<features_all.length; i++) {
			if (features_all[i] == null) {
				throw new IllegalStateException("feature is null");
			}
			if (features_all[i].getId() != i) {
				throw new IllegalStateException("no maching index = " + i + " > " + features_all[i]);
			}
		}*/
	}
	
	public static Features load(String cfgClassName, IFeaturesMerger merger) throws Exception {
		
		Set<IFeature> result_set = new TreeSet<IFeature>();
		
		Features f_m = createNewFeatures(cfgClassName);
		Features f_p = load();
		
		IFeature[] f_m_all = f_m.getFeatures();
		IFeature[] f_p_all = f_p == null ? new IFeature[0] : f_p.getFeatures();
		
		for (int i=0; i<f_p_all.length; i++) {
			result_set.add(f_p_all[i]);
		}
		
		for (int i=0; i<f_m_all.length; i++) {
			
			IFeature cur_m = f_m_all[i];
			
			if (result_set.contains(cur_m)) {
				
				Feature cur_p_f = null;
				for (IFeature f : result_set) {
					if (f.equals(cur_m)) {
						cur_p_f = (Feature) f;
						break;
					}
				}
				Feature cur_m_f = (Feature) cur_m;
				
				if (cur_p_f instanceof FeatureArray && cur_m_f instanceof FeatureSingle) {
					result_set.remove(cur_p_f);
					result_set.add(cur_m_f);
					System.out.println("Replaced feature : " + cur_p_f.getName());
				} if (cur_p_f instanceof FeatureSingle && cur_m_f instanceof FeatureArray) {
					throw new UnsupportedOperationException();
				} else {
					//cur_p_f.merge(cur_m_f);
					merger.merge(cur_p_f, cur_m_f);
					//System.out.println("CHECK MERGER: Merged feature : " + cur_p_f.getName());
				}
				
			} else {
				result_set.add(cur_m);
			}
		}
		
		
		return new Features(result_set.toArray(new IFeature[0]));
	}
	
	public static Features load() {
		return load(FEATURES_FILE_NAME);
	}
	
	
	public static Features load(String fileName) {
		List<IFeature> result = null;//new TreeSet<IFeature>();
		//System.out.println("Path=" + (new File(".")).getAbsolutePath());
		try {
			File org = new File(fileName);
			if (!org.exists()) {
				System.out.println("FeaturesPersistency.load: org not found");
				File backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
				if (!backup.exists()) {
					System.out.println("FeaturesPersistency.load: backup not found");
					System.out.println("FILE NOT FOUND: " + fileName + ", probably it will be created later.");
				} else {
					System.out.print("FeaturesPersistency.load: rename backup to org - ");
					boolean ok = backup.renameTo(org);
					System.out.println("" + ok);
				}
			}
			
			//System.out.println("FeaturesPersistency.load: reading binary");
			InputStream is = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(is);
			
			IFeature cur_f = null;
			while ((cur_f = (IFeature) ois.readObject()) != null) {
				if (result == null) {
					result =new ArrayList<IFeature>();
				}
				result.add(cur_f);
			}
			ois.close();
			is.close();
		} catch (FileNotFoundException fnfe) {
			//
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("ERROR OPENING: " + FEATURES_FILE_NAME + ", it will be created after the first game.");
		}
		
		if (result == null) {
			return null;
		}
		
		return new Features(result.toArray(new IFeature[0]));
	}
	
	public void store(String fileName) {
		try {
			//Delete backup
			File backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
			if (backup.exists()) {
				//System.out.print("FeaturesPersistency.store: del backup - ");
				boolean ok = backup.delete();
				//System.out.println("" + ok);
			}
			
			//Rename the org to backup
			File org = new File(fileName);
			//System.out.print("FeaturesPersistency.store: rename org to backup - ");
			backup = new File(fileName + FEATURES_FILE_NAME_BACKUP_SUFFIX);
			boolean ok = org.renameTo(backup);
			//System.out.println("" + ok);
			
			//System.out.println("FeaturesPersistency.store: writing binary");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			for (IFeature f: features_all.values()) {
				oos.writeObject(f);
			}
			oos.writeObject(null);
			oos.flush();
			oos.close();
			
			//System.out.print("FeaturesPersistency.store: del backup - ");
			boolean ok1 = backup.delete();
			//System.out.println("" + ok1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void store() {
		store(FEATURES_FILE_NAME);
	}
	
	public String toString() {
		String result = "";
		for (IFeature f: features_all.values()) {
			result += f + ", " + "\r\n";
		}
		return result;
	}
	
	public static void main(String[] args) {
		//Features f = Features.getSingleton();
		//System.out.println(f);
		//f.store();
		
		//Map<Integer, Feature> loaded = f.load();
		//System.out.println(loaded);
		
		//System.out.println(Features.getSingleton().get(Features.FEATURE_ID_MOBILITY_BISHOP));
		//Feature f = new Feature(1, "PINKO", 1, 3, 2, 2);
		//int index = f.findBucket(2.7);
		//while (true) {
			/*int counter = 0;
			while (counter < 1000000) {
				double amount = Math.random() >= 0.5 ? 0.01 : -0.01;
				f.adjust(amount, 0.1);
				
				counter++;
			}
			System.out.println(f);*/
		//}
		//System.out.println(index);
	}

	public static void dump(IFeature[] fs) {
		
		for (int i=0; i<fs.length; i++) {
			System.out.println(fs[i] + ", ");
		}
	}


	public static void toJavaCode(IFeature[] features, String suffix) {
		
		for (int i = 0; i < features.length; i++) {
			
			if (features[i] != null) {
				
				System.out.println(features[i].toJavaCode(suffix));
			
			} else {
				
				//System.out.println("EMPTY index " + i);
			}
		}
	}
}



package bagaturchess.learning.impl.features;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.impl.features.impl1.FeaturePST;
import bagaturchess.learning.impl.signals.Signals;



public class Features {
	
	private static final String FEATURES_FILE_NAME = "./features.bin";
	private static final String FEATURES_FILE_NAME_BACKUP_SUFFIX = ".backup";
	
	private IFeature[] features_all;
	private IFeature[] features_pst;
	private IFeature[][] features_by_complexity;
	
	//private static Features persistentFeatures;
	//private static Features currentFeatures;
	
	
	/*public static final Features getSingleton() {
		throw new UnsupportedOperationException();
	}*/
	
	
	/*public static Features getPersistentFeatures() {
		
		if (persistentFeatures == null) {
			Set<IFeature> featuresSet = load();
			if (featuresSet != null) {
				persistentFeatures = new Features(featuresSet.toArray(new IFeature[0]));
			} else {
				persistentFeatures = createNewFeatures();
			}
		}
		
		return persistentFeatures;
	}*/
	
	//public static void setPersistentFeatures(Features _persistentFeatures) {
	//	persistentFeatures = _persistentFeatures;
	//}
	
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
		return new Signals(this);
	}
	
	/*public static Features getCurrentFeatures() {
		return currentFeatures;
	}
	
	public static void setCurrentFeatures(Features _currentFeatures) {
		currentFeatures = _currentFeatures;
	}*/
	
	/*public void clearStat() {
		for (int i=0; i<features_all.length; i++) {
			features_all[i].clearStat();
		}
	}*/
	
	/*public void fitBounds() {
		for (int i=0; i<features_all.length; i++) {
			features_all[i].fitBounds(0.25);
		}
	}
	
	public void setTo021() {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.setTo021();
		}
	}
	
	public void setAverageToCurrent() {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.setAverageToCurrent();
		}		
	}
	
	public void correct() {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.correct();
		}
	}
	
	public void scale(Signals signals, double openningPart) {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.scale(signals.getSignal(f.getId()), openningPart);
		}
	}
	
	public void sign(Signals signals, double openningPart) {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.sign(signals.getSignal(f.getId()), openningPart);
		}
	}
	
	public void multiplyAvarageAndCopyToCurrent(Features features) {
		for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.multiplyAvarageAndCopyToCurrent(features.get(f.getId()));
		}	
	}*/
	
	public IFeature[] getFeatures() {
		return features_all;
	}
	
	public IFeature get(int id) {
		return features_all[id];
	}
	
	public IFeature[] getAllByComplexity(int complexity) {
		return features_by_complexity[complexity];
	}
	
	public IFeature[][] getAllByComplexity() {
		return features_by_complexity;
	}
	
	public IFeature[] getPSTFeatures() {
		return features_pst;
	}
	
	private Features(IFeature[] all) {
		
		features_all = all;
		/*for (int i=0; i<features_all.length; i++) {
			IFeature f = features_all[i];
			f.setAdjustable(true);
		}*/
		
		checkConsistency();
		
		features_by_complexity = new IFeature[IFeatureComplexity.MAX][];
		for (int i=0; i<IFeatureComplexity.MAX; i++) {
			List<IFeature> byComplexity = new ArrayList<IFeature>();	
			for (IFeature f: features_all) {
				if (f.getComplexity() == i) {
					byComplexity.add(f);
				}
			}
			features_by_complexity[i] = byComplexity.toArray(new IFeature[0]);
		}
		
		List<IFeature> pst = new ArrayList<IFeature>();	
		for (IFeature f: features_all) {
			if (f instanceof FeaturePST) {
				pst.add(f);
			}
		}
		
		features_pst = pst.toArray(new IFeature[0]); 
	}
	
	private void checkConsistency() {
		for (int i=0; i<features_all.length; i++) {
			if (features_all[i] == null) {
				throw new IllegalStateException("feature is null");
			}
			if (features_all[i].getId() != i) {
				throw new IllegalStateException("no maching index = " + i + " > " + features_all[i]);
			}
		}
	}
	
	private static Set<IFeature> load() {
		Set<IFeature> result = null;//new TreeSet<IFeature>();
		//System.out.println("Path=" + (new File(".")).getAbsolutePath());
		try {
			File org = new File(FEATURES_FILE_NAME);
			if (!org.exists()) {
				System.out.println("FeaturesPersistency.load: org not found");
				File backup = new File(FEATURES_FILE_NAME + FEATURES_FILE_NAME_BACKUP_SUFFIX);
				if (!backup.exists()) {
					System.out.println("FeaturesPersistency.load: backup not found");
					System.out.println("FILE NOT FOUND: " + FEATURES_FILE_NAME + ", probably it will be created later.");
				} else {
					System.out.print("FeaturesPersistency.load: rename backup to org - ");
					boolean ok = backup.renameTo(org);
					System.out.println("" + ok);
				}
			}
			
			//System.out.println("FeaturesPersistency.load: reading binary");
			InputStream is = new FileInputStream(FEATURES_FILE_NAME);
			ObjectInputStream ois = new ObjectInputStream(is);
			
			IFeature cur_f = null;
			while ((cur_f = (IFeature) ois.readObject()) != null) {
				if (result == null) {
					result =new TreeSet<IFeature>();
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
		
		return result;
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
			for (int i=0; i<features_all.length; i++) {
				oos.writeObject(features_all[i]);
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
		for (int i=0; i<features_all.length; i++) {
			result += features_all[i] + ", " + "\r\n";
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
}

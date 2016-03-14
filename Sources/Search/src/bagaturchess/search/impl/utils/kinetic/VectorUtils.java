package bagaturchess.search.impl.utils.kinetic;


public class VectorUtils {
	
	
	public static int calcScalar(KineticData_Position p) {
		int result = 0;
		
		for (int i=0; i<p.evals.length; i++) {
			result += (p.evals[i] / (i + 1));
		}
		
		return result;
	}
	
	
	public static KineticData_Position calcVector(
			KineticData_Position my1, //1
			KineticData_Position op1, //2
			KineticData_Position op2, //2
			KineticData_Position my2 //3
		) {
		
		my1 = VectorUtils.shift(my1, 0);
		op1 = VectorUtils.shift(op1, 1);
		op2 = VectorUtils.shift(op2, 1);
		my2 = VectorUtils.shift(my2, 2);
		
		KineticData_Position my_diff = VectorUtils.substract(my2, my1);
		KineticData_Position op_diff_inversed = VectorUtils.inverse(VectorUtils.substract(op2, op1));
		
		KineticData_Position my_sum = VectorUtils.sum(my_diff, op_diff_inversed);
		
		return my_sum;
	}
	
	
	public static KineticData_Position substract(KineticData_Position first, KineticData_Position second) {
		KineticData_Position diff = new KineticData_Position();
		
		for (int i=0; i<first.evals.length; i++) {
			
			if (first.counts[i] < 0 || second.counts[i] < 0) {
				throw new IllegalStateException();
			}
			
			if (first.counts[i] != 0 && second.counts[i] != 0) {
				diff.evals[i] = first.evals[i] - second.evals[i];	
				
			} else if (first.counts[i] != 0 && second.counts[i] == 0) {
				diff.evals[i] = first.evals[i]; /*- second.evals[i];*/
				
			} else if (first.counts[i] == 0 && second.counts[i] != 0) {
				diff.evals[i] = /*first.evals[i]*/ - second.evals[i];
				
			} else {
				//Do nothing
			}
			
			diff.counts[i] = first.counts[i] + second.counts[i];
		}
		
		return diff;
	}

	
	public static KineticData_Position sum(KineticData_Position first, KineticData_Position second) {
		KineticData_Position diff = new KineticData_Position();
		
		for (int i=0; i<first.evals.length; i++) {
			diff.evals[i] = first.evals[i] + second.evals[i];
			diff.counts[i] = first.counts[i] + second.counts[i];
		}
		
		return diff;
	}

	
	public static KineticData_Position inverse(KineticData_Position that) {
		KineticData_Position inverse = new KineticData_Position();
		
		for (int i=0; i<that.evals.length; i++) {
			inverse.evals[i] = -that.evals[i];
			inverse.counts[i] = that.counts[i];
		}
		
		return inverse;
	}
	
	public static KineticData_Position div(KineticData_Position that, int number) {
		
		KineticData_Position div = new KineticData_Position();
		
		for (int i=0; i<that.evals.length; i++) {
			div.evals[i] = that.evals[i] / number;
		}
		
		return div;
	}
	
	public static KineticData_Position shift(KineticData_Position that, int shift) {
		KineticData_Position result = new KineticData_Position();
		
		for (int i=0; i<that.evals.length; i++) {
			if (i + shift == that.evals.length) {
				break;
			}
			result.evals[i + shift] = that.evals[i];
			result.counts[i + shift] = that.counts[i];
		}
		
		return result;
	}
}

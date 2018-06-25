package ca.bcit.net.algo;

import ca.bcit.net.Network;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.Collection;
import java.util.HashMap;

//Parent class for all algorithms
public abstract class RMSAAlgorithm {

    //Private class constant
	private static final HashMap<String, RMSAAlgorithm> registeredAlgorithms = new HashMap<>();
	//Each registeredAlgorithms is key value pair of <String, RMSAAlgoritm>

    //Run class method registerAlgorithm for each RMSA algorithm
	static {
		registerAlgorithm(new SPF());
		registerAlgorithm(new AMRA());
		registerAlgorithm(new MNC());
	}

	//Class method for adding a new key-value pair to registeredAlgorithms
	private static void registerAlgorithm(RMSAAlgorithm algorithm) {
		if (!registeredAlgorithms.containsKey(algorithm.getName()))
			registeredAlgorithms.put(algorithm.getName(), algorithm);
	}

	//Class method for getting all values for all key-value pairs stored in registeredAlgorithm
	public static Collection<RMSAAlgorithm> getRegisteredAlgorithms() {
		return registeredAlgorithms.values();
	}

	//method for getting name of the algorithm
	@Override
	public String toString() {
		return getName();
	}

    //Define private class method getName
	protected abstract String getName();

	//Define public class method allocateDemand with input variable Demand and Network and of class DemandAllocationResult
	public abstract DemandAllocationResult allocateDemand(Demand demand, Network network);
}

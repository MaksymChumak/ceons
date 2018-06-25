package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.List;

//Define SPF as inheriting from RMSAAlgorithm
public class SPF extends RMSAAlgorithm {

    //Define getName to return the string: "SPF"
    @Override
    protected String getName() {
        return "SPF";
    }

    //Define opration for public class method allocateDemand
    @Override
    public DemandAllocationResult allocateDemand(Demand demand, Network network) {

        //Calculate volume as: [(input volume / 10) -1] <-- rounded up
        int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;

        //Get candidatePaths by calling demand.getCandidatePaths, setting backup to false
        List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);

        //Sort the candidate paths for length
        sortByLength(network, volume, candidatePaths);

        //Checks for if there are no paths
        if (candidatePaths.isEmpty())
            return DemandAllocationResult.NO_SPECTRUM;

        boolean workingPathSuccess = false;

        //Checks using demand.allocate
        try {
            for (PartedPath path : candidatePaths)
                if (demand.allocate(network, path)) {
                    workingPathSuccess = true;
                    break;
                }

        } catch (NetworkException storage) {
            workingPathSuccess = false;
            return DemandAllocationResult.NO_REGENERATORS;
        }

        //If previous check gives negative result
        if (!workingPathSuccess)
            return DemandAllocationResult.NO_SPECTRUM;

        //Checks using demand.allocateBackup
        if (demand.allocateBackup()) {
            volume = (int) Math.ceil(demand.getSqueezedVolume() / 10) - 1;

            if (candidatePaths.isEmpty())
                return new DemandAllocationResult(
                        demand.getWorkingPath());
            for (PartedPath path : candidatePaths)
                if (demand.allocate(network, path))
                    return new DemandAllocationResult(demand.getWorkingPath(), demand.getBackupPath());

            return new DemandAllocationResult(demand.getWorkingPath());
        }

        return new DemandAllocationResult(demand.getWorkingPath());
    }

    //Define class method for sorting and returning list of calculated candidate paths and
    private List<PartedPath> sortByLength(Network network, int volume, List<PartedPath> candidatePaths) {
        pathLoop:
        for (PartedPath path : candidatePaths) {
            path.setMetric(path.getPath().getLength());

            // choosing modulations for parts
            for (PathPart part : path) {
                for (Modulation modulation : network.getAllowedModulations())
                    if (modulation.modulationDistances[volume] >= part.getLength()) {
                        part.setModulation(modulation, 1);
                        break;
                    }

                if (part.getModulation() == null)
                    continue pathLoop;
            }
        }
        for (int i = 0; i < candidatePaths.size(); i++)
            for (PathPart spec: candidatePaths.get(i).getParts()){
                if (spec.getOccupiedSlicesPercentage() > 80.0) {
                    candidatePaths.remove(i);
                    i--;
                }
            }

        candidatePaths.sort(PartedPath::compareTo);
        return candidatePaths;
    }
}

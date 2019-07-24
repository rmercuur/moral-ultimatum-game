package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import repast.simphony.random.RandomHelper;
import ultimateValuesEclipse.Helper;
import values.Fairness;
import values.Value;
import values.Wealth;

/* This is the agent used in the simulation to simulate value-based agents
 *
 */
public class ValueBasedAgentDivide extends Agent {
    List<Value> values;
    Value wealth;
    Value fairness;
    int myBestDemand;
    int myThreshold;
    
    public ValueBasedAgentDivide(int ID, boolean isProposer) {
		super(ID,isProposer); 
        
        //If you want to make truncated agent
        double leftBound =-100;
        double rightBound=100;
        
        valueDifference = -200;
        while(valueDifference < leftBound || valueDifference > rightBound){
        	valueDifference = RandomHelper.createNormal(Helper.getParams().getDouble("valueDifferenceMean"),
        			Helper.getParams().getDouble("valueDifferenceSD")).nextDouble();
        } 
        wealth =new Wealth(1+(valueDifference/2));
        fairness=new Fairness(1-(valueDifference/2));
        values =new ArrayList<Value>();
        values.add(wealth);
        values.add(fairness);
        
        myBestDemand = calculateMyBestDemand();
        myThreshold = calculateMyThreshold();
        
    }
    
//    public ValueBasedAgentDivide(int ID, double valueDifference, boolean isProposer) {
//		super(ID,isProposer);
//        this.valueDifference = valueDifference;
//   
//        wealth =new Wealth(1+(valueDifference/2));
//        fairness=new Fairness(1-(valueDifference/2));
//        values =new ArrayList<Value>();
//        values.add(wealth);
//        values.add(fairness);
//    }
    

	public int calculateMyBestDemand(){
		TreeMap<Double, Integer> demandToUtility = new TreeMap<Double, Integer>();
		
		for(int demand = 0; demand < (Helper.getParams().getInteger("pieSize") +1); demand++){
		
			double utility = wealth.thresholdDivideUtility(demand); 
					utility+=fairness.thresholdDivideUtility(demand);
			demandToUtility.put(utility, demand); 
		}
		return demandToUtility.lastEntry().getValue(); 
	}
	
	public int calculateMyThreshold(){
		List<Integer> acceptableDemands =new ArrayList<Integer>();
		for(int demand = 0; demand < (Helper.getParams().getInteger("pieSize") +1); demand++){
			int offer = Helper.getParams().getInteger("pieSize") -demand;
			double acceptUtility = wealth.thresholdDivideUtility(offer) ;
			acceptUtility += fairness.thresholdDivideUtility(offer);
			double rejectUtility = wealth.thresholdDivideUtility(1) ; //NB: zodat die gelijk aan R is...
			rejectUtility += fairness.thresholdDivideUtility((Helper.getParams().getInteger("pieSize") /2)); //For fairness purposses its as if it was an even split;
			if(acceptUtility > rejectUtility) acceptableDemands.add(demand);
		}
		return acceptableDemands.isEmpty() ? 0:acceptableDemands.stream().mapToInt(i -> i).max().getAsInt(); //if empty, i accept no demand above 0, else return maximum demand that is still acceptable!
	}
	
    @Override
    public int myPropose(Agent responder) {
    	return getMyBestDemand();
    }

	
    @Override
    public boolean myRespond(int demand, Agent proposer) {
    	return demand <= getMyThreshold();
    }

	@Override
	public void update() {
	//	wealth.updateSatisfaction(myGame.getOutcome(this)); //does this work, returns something but we dont care?
	//	fairness.updateSatisfaction(myGame.getOutcome(this));
	}
	
    
    public double getWStrength(){
    	return wealth.getStrength();
    }
    public double getFStrength(){
    	return fairness.getStrength();
    }
    public double getWFStrength(){
    	return getWStrength()/getFStrength();
    }
    public double getWNeed(){
    	return wealth.getNeed();
    }
    public double getFNeed(){
    	return fairness.getNeed();
    }

	public int getMyBestDemand() {
		return myBestDemand;
	}

	public int getMyThreshold() {
		return myThreshold;
	}
    
}

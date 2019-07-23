package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.TreeMap;

import repast.simphony.random.RandomHelper;
import ultimateValuesEclipse.Helper;
import values.Fairness;
import values.Value;
import values.Wealth;

/*
 * This is a possible new version that combines the value and normative agent.
 * However:
 * - it uses a new version of the value-based agent that first calculates best demand and threshold based on its values, which possibly makes it faster
 * - it uses an old (BROKEN) version of the normative-agent that has an average of its OWN actions
 */
public class TestValueNormAgent extends Agent {
	List<Value> values;
    Value wealth;
    Value fairness;
    int myBestDemand;
    int myThreshold;

	List<Integer> seenDemands;
	List<Integer> seenRespondsAccepted;
	List<Integer> seenRespondsRejected;
	int initialAction;
	int initialAcceptRate;

    
    boolean valueOrNorms;
    
	public TestValueNormAgent(int ID, boolean isProposer) {
		super(ID,isProposer);
		
		//Values 
        double leftBound =-100; //If you want to make truncated agent
        double rightBound=100; //If you want to make truncated agent
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
        
        //Norms
        seenDemands=new ArrayList<Integer>();
		seenRespondsAccepted=new ArrayList<Integer>();
		seenRespondsRejected=new ArrayList<Integer>();	
		initialAction = Helper.getParams().getInteger("initialActionNorm");
		initialAcceptRate = Helper.getParams().getInteger("initialAcceptRateNorm");
	
		
		//Value-Norm-Agent
		 valueOrNorms = true; //true = Value, norm = False;
	}

	public int calculateMyBestDemand(){
		TreeMap<Double, Integer> demandToUtility = new TreeMap<Double, Integer>();
		
		for(int demand = 0; demand < (Helper.getParams().getInteger("pieSize") +1); demand++){
	//		System.out.println("For:" + demand);
			
			double utility = wealth.thresholdDivideUtility(demand); 
					utility+=fairness.thresholdDivideUtility(demand);
			demandToUtility.put(utility, demand); 
		}
		return (int) demandToUtility.lastEntry().getValue(); 
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
		
		return acceptableDemands.stream().mapToInt(i -> i).max().getAsInt(); //maximum demand that is still acceptable!
	}
	
	@Override
	public int myPropose(Agent responder) {
		int demand =0;
		if(valueOrNorms){
			demand= myBestDemand;
		}
		else{		
			if(!seenRespondsAccepted.isEmpty() &&!seenRespondsRejected.isEmpty()){
				demand = (int) //do the norm
						(seenRespondsRejected.stream().mapToDouble(a -> a).min().getAsDouble() +
						seenRespondsAccepted.stream().mapToDouble(a -> a).max().getAsDouble()) /
						2;}
			else{//if no norm available
				if(initialAction ==0){ //do the action first-round agents do
					demand = -10; 
					double mean = 0.5618 * Helper.getParams().getInteger("pieSize");
					double sd = 0.1289 * Helper.getParams().getInteger("pieSize");
					while(demand < 0 || demand > Helper.getParams().getInteger("pieSize")){
						demand= RandomHelper.createNormal(mean, sd).nextInt();
					}
				}
				if(initialAction ==1){
					demand= 0;				
				}
				if(initialAction ==2){ //0 half-pie
					demand= RandomHelper.createUniform(0,0.5*Helper.getParams().getInteger("pieSize")).nextInt();
				}
				if(initialAction ==3){ //0 - pie
					demand= RandomHelper.createUniform(0,Helper.getParams().getInteger("pieSize")).nextInt();
				}	
				if(initialAction ==4){ //0.5- pie
					demand= RandomHelper.createUniform(0.5*Helper.getParams().getInteger("pieSize"),Helper.getParams().getInteger("pieSize")).nextInt();				
				}
				if(initialAction ==5){
					demand= 1000;
				}
			}
		}
		return demand;
	}

	@Override
	public boolean myRespond(int demand, Agent proposer) {
		boolean respond;
		if(valueOrNorms){ //act out of values
			if(demand<=myThreshold) respond = true; //threshold is still acceptable
			else respond =false;
		}else{ //act out of norms
			if(seenDemands.isEmpty()){
				double acceptRate =0.0;
				if(initialAcceptRate ==0){ //do the action first-round agents do

					double mean = 0.806;
					double sd = 0.395;
					acceptRate= RandomHelper.createNormal(mean, sd).nextDouble(); //NB: are extreme values a problem? don't thinks o
				}
				
				if(initialAcceptRate ==1){ //do the action first-round agents do
					acceptRate= RandomHelper.createUniform(0,0.0).nextDouble();
				}
				
				if(initialAcceptRate ==2){ //do the action first-round agents do
					acceptRate= RandomHelper.createUniform(0,0.5).nextDouble();
				}
				if(initialAcceptRate ==3){ //do the action first-round agents do
					acceptRate= RandomHelper.createUniform(0,1.0).nextDouble();
				}
				if(initialAcceptRate ==4){
					acceptRate= RandomHelper.createUniform(0.75,1.0).nextDouble();
				}
				if(initialAcceptRate ==5){
					acceptRate= RandomHelper.createUniform(1.0,1.0).nextDouble();
				}
				respond= RandomHelper.createUniform(0,1).nextDouble() <acceptRate;
			}
			else{
				OptionalDouble averageSeenDemand = (OptionalDouble) seenDemands.stream().mapToDouble(a -> a).average();
				int threshold = (int) averageSeenDemand.getAsDouble();
				respond = demand <= threshold;
			}
		}		
		return respond;
	}
	
	
	@Override
	public void update() {
		//Normative Update
		seenDemands.add(myGame.getDemand());
		
		if(myGame.isAccepted()){
			seenRespondsAccepted.add(myGame.getDemand());
		}
		else{
			seenRespondsRejected.add(myGame.getDemand());
		}
	}

}

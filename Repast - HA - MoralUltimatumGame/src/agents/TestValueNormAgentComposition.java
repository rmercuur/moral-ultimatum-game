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
public class TestValueNormAgentComposition extends Agent {
	ValueBasedAgentDivide myValueBasedAgent;
	NormativeAgent5 myNormativeAgent;
	boolean valueOrNorms;
	double valueWeight;
	double normWeight;
	double noiseWeight;
	
	public TestValueNormAgentComposition(int ID, boolean isProposer) {
		super(ID,isProposer);
		myValueBasedAgent=new ValueBasedAgentDivide(1000,isProposer);
		myNormativeAgent=new NormativeAgent5(1000,isProposer);
		
		noiseWeight = Helper.getParams().getDouble("noiseWeight");
		double remainingWeight = 1.0 - noiseWeight;
		double mean = Helper.getParams().getDouble("valueDifferenceMean");
		double sd = Helper.getParams().getDouble("valueDifferenceSD");
		valueWeight = 100; //higher than 1;
		while(valueWeight < 0 || valueWeight > remainingWeight){
			valueWeight= RandomHelper.createNormal(mean, sd).nextDouble();
		} 
		normWeight = remainingWeight-valueWeight;
	}
	
	//For Unit Test Purposes
	public TestValueNormAgentComposition(int ID, double valueDifference, boolean isProposer) {
		super(ID,isProposer);
		myValueBasedAgent=new ValueBasedAgentDivide(1000,valueDifference, isProposer);//using the Unit Test Contructor
		myNormativeAgent=new NormativeAgent5(1000,isProposer);
		double mean = Helper.getParams().getDouble("valueDifferenceMean");
		double sd = Helper.getParams().getDouble("valueDifferenceSD");
		double valueWeight = 100; //higher than 1;
		while(valueWeight < 0 || valueWeight > 1){
			valueWeight= RandomHelper.createNormal(mean, sd).nextDouble();
		} 
		normWeight = 1-valueWeight;
	}
	
	@Override
	public int myPropose(Agent responder) {
		int valueDemand= myValueBasedAgent.myPropose(responder);
		int normDemand =myNormativeAgent.myPropose(responder);
		int noiseDemand = RandomHelper.createUniform(0, Helper.getPieSize()).nextInt();
		
		double demand = valueWeight * valueDemand + normWeight * normDemand + noiseWeight * noiseDemand;
		return (int) Math.round(demand);
	}
	
	@Override
	public boolean myRespond(int demand, Agent proposer) {
		int valueThreshold =myValueBasedAgent.getMyThreshold();
		int normThreshold =myNormativeAgent.getMyThreshold();
		int noiseThreshold =  RandomHelper.createUniform(0, Helper.getPieSize()).nextInt();
		
		double threshold= valueWeight * valueThreshold + normWeight +normThreshold + noiseWeight *noiseThreshold;
		return demand <= threshold;
	}
	
	@Override
	public void update() {
		myNormativeAgent.seenDemands.add(myGame.getDemand());
		//zou kunnen dat beiden runnen toch
		//alleen de myNormDemand lijkt twee keer te runnen
		if(myGame.isAccepted()){
			myNormativeAgent.seenRespondsAccepted.add(myGame.getDemand());
		}
		else{
			myNormativeAgent.seenRespondsRejected.add(myGame.getDemand());
		}
	}
	
	public int getMyValueDemand(){
		Agent responder= null; //hack
		return myValueBasedAgent.myPropose(responder);
	}
	
	public int getMyNormDemand(){
		Agent responder= null; //hack
		return myNormativeAgent.myPropose(responder);
	}
	
	public int getMyValueThreshold(){
		return myValueBasedAgent.getMyThreshold();
	}
	
	public int getMyNormThreshold(){
		return myNormativeAgent.getMyThreshold();
	}
	
	@Override
	public double getValueDifference() {
		return myValueBasedAgent.getValueDifference();
	}
	
	public double getMyValueWeight() {
		return valueWeight;
	}
}

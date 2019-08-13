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
public class TestValueNormAgentCompisition extends Agent {
	ValueBasedAgentDivide myValueBasedAgent;
	NormativeAgent5 myNormativeAgent;
	boolean valueOrNorms;
	
	public TestValueNormAgentCompisition(int ID, boolean isProposer) {
		super(ID,isProposer);
		myValueBasedAgent=new ValueBasedAgentDivide(1000,isProposer);
		myNormativeAgent=new NormativeAgent5(1000,isProposer);
	
		 valueOrNorms = false; //true = Value, norm = False;
	}
	@Override
	public int myPropose(Agent responder) {
		int valueDemand= myValueBasedAgent.myPropose(responder);
		int normDemand =myNormativeAgent.myPropose(responder);
		
		double valueWeight= 0.5; //1 - 0.5/Helper.getParams().getInteger("EndTime");
		double normWeight = 0.5; //0 + 0.5/Helper.getParams().getInteger("EndTime");
		

		
		double demand = valueWeight * valueDemand + normWeight * normDemand;
		return (int) demand;
	}
	
	@Override
	public boolean myRespond(int demand, Agent proposer) {
		int valueThreshold =myValueBasedAgent.getMyThreshold();
		int normThreshold =myNormativeAgent.getMyThreshold();
		
		double valueWeight= 1 - 0.5/Helper.getParams().getInteger("EndTime");
		double normWeight = 0 + 0.5/Helper.getParams().getInteger("EndTime");
		
		double threshold= valueWeight * valueThreshold + normWeight +normThreshold ;
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
}

package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import repast.simphony.random.RandomHelper;
import ultimateValuesEclipse.Helper;
/*
 * Act out of norm or does action humans do the first-round
 * 
 */
public class NormativeAgent5 extends Agent {
	List<Integer> seenDemands;
	List<Integer> seenRespondsAccepted;
	List<Integer> seenRespondsRejected;
	int initialAction;
	int initialAcceptRate;
	
	public NormativeAgent5(int ID, boolean isProposer) {
		super(ID,isProposer);
		seenDemands=new ArrayList<Integer>();
		seenRespondsAccepted=new ArrayList<Integer>();
		seenRespondsRejected=new ArrayList<Integer>();	
		initialAction = Helper.getParams().getInteger("initialActionNorm");
		initialAcceptRate = Helper.getParams().getInteger("initialAcceptRateNorm");
		//0 = Human
		//1 = mu:0PieSize
		//2 = mu:0.25PieSize
		//3 = mu:0.5PieSize
		//4 = mu:0.75PieSize
		//5 = mu:1.0PieSize
			valueDifference = -200; //This is so you can find the normative agent from looking at this;
	}

	@Override
	public void update() {
		seenDemands.add(myGame.getDemand());
	
		if(myGame.isAccepted()){
			seenRespondsAccepted.add(myGame.getDemand());
		}
		else{
			seenRespondsRejected.add(myGame.getDemand());
		}
	}

	@Override
	public int myPropose(Agent responder) {
		int demand =0;
		String state = null;
		if(seenRespondsAccepted.isEmpty() &&seenRespondsRejected.isEmpty()) state = "firstTick";
		if(seenRespondsAccepted.isEmpty() &&!seenRespondsRejected.isEmpty()) state = "onlyRejects";
		if(!seenRespondsAccepted.isEmpty()&& seenRespondsRejected.isEmpty()) state = "onlyAccepts";
		if(!seenRespondsAccepted.isEmpty()&& !seenRespondsRejected.isEmpty()) state = "bothAcceptsAndRejects";
		
		switch(state) {
			case "firstTick":
				switch (initialAction) {
					case 0:
						demand = -10; 
						double mean = 0.5618 * Helper.getParams().getInteger("pieSize");
						double sd = 0.1289 * Helper.getParams().getInteger("pieSize");
						while(demand < 0 || demand > Helper.getParams().getInteger("pieSize")){
							demand= RandomHelper.createNormal(mean, sd).nextInt();
						break;
						}
					case 1: demand =0; break;
					case 2: demand= RandomHelper.createUniform(0,0.5*Helper.getParams().getInteger("pieSize")).nextInt();break;
					case 3: demand= RandomHelper.createUniform(0,Helper.getParams().getInteger("pieSize")).nextInt();break;
					case 4: demand= RandomHelper.createUniform(0.5*Helper.getParams().getInteger("pieSize"),Helper.getParams().getInteger("pieSize")).nextInt();	break;			
					case 5: demand= 1000;break;
				}
				break;
			case "onlyRejects":
				demand = (int) (seenRespondsRejected.stream().mapToDouble(a -> a).min().getAsDouble() + 
						0.5*Helper.getParams().getInteger("pieSize"))/
						2; 
				break;
			case "onlyAccepts":
				demand = (int) (seenRespondsAccepted.stream().mapToDouble(a -> a).max().getAsDouble() + 
						Helper.getParams().getInteger("pieSize"))/
						2; 
				break;
			case "bothAcceptsAndRejects":
				demand = (int) //do the norm
					(seenRespondsRejected.stream().mapToDouble(a -> a).min().getAsDouble() +
					seenRespondsAccepted.stream().mapToDouble(a -> a).max().getAsDouble()) /
					2; 
				break;
		}
		return demand;
	}

	@Override
	public boolean myRespond(int demand, Agent proposer) {
		boolean accept;
		
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
			accept= RandomHelper.createUniform(0,1).nextDouble() <acceptRate;
		}
		else{
			accept = demand <= getMyThreshold();
		}
		return accept;
	}

	public int getMyThreshold(){
		int threshold =0;
		if(seenDemands.isEmpty()){ //This is not used, just for completeness in data
			threshold = (int) 0.5 * Helper.getParams().getInteger("pieSize");
		}
		else{	
			OptionalDouble averageSeenDemand = (OptionalDouble) seenDemands.stream().mapToDouble(a -> a).average();
			threshold= (int) averageSeenDemand.getAsDouble();
		} 
		return threshold;
	}
}

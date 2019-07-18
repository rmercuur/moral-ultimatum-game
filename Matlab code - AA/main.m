%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Moral analyzer - Ultimatum game
% Luciano C. Siebert; Rijk Mercuur
% TU DELFT
% 07.2019
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clc
clear all
close all
% data_simulation=importfile('DS1_IndividualResponseAndValueDifference.2019.Jul.16.15_03_32.txt');
data_simulation=importfile('DS2_IndividualResponseAndValueDifference.2019.Jul.16.15_07_06.txt');
% ["tick", "ID", "LastAction", "ValueDifference"]
% - the tick column signifies the round
% - ID is the agent ID obvioulsy
% - LastAction: if it is between 0 and 1000 it specifies the demands if it -1337 it specifies a reject if its 1337 its an accept
% -ValueDifference: if its -200 it means its a normative agent, any other number means it is the actually di for the value-agent

%MAIN STATS OF THE DATASET
% 16 players
% 30 rounds
% 32 games each round
% Total of 960 games
% Every round a given agent can be assigned as value-based or a normative agent
% ROLES (proposer or respondant) is fixed for every round:
%   * Players ID 0 until ID 7 are PROPOSERS
%   * Players ID 8 until ID 15 are RESPONDERS

%%% I DONT UNDERSTAND WHY EVERY AGENT IS BOTH VALUE-BASED AND NORMATIVE AT
%%% THE SAME ROUND (AND THEREFORE PROPOSES OR RESPOND TWICE EVERY ROUND
%%% (TICK)

%% Parameters
pie_size=1000;

%% Analyzing
%PLOTTING SCATTER FOR VALUE-BASED AGENTS
id_current=data_simulation.ValueDifference ~= -200 & data_simulation.LastAction ~=1337 & data_simulation.LastAction ~=-1337;
offer=data_simulation.LastAction(id_current);
i_d=data_simulation.ValueDifference(id_current);
scatter(i_d,offer)


% % %  STOPPED HERE
% 
% for cont_ID = 0:7 %PROPOSER
% %    Find the offer but ONLY for Value-Based agents
%     offer = data_simulation.LastAction (data_simulation.ID == cont_ID & data_simulation.ValueDifference ~= -200); 
%     s_w = offer / 1000;
%     s_f = 1 - (abs(0.5 * pie_size - offer) / (0.5 * pie_size));
% 
%     %Mean deviation from 50% of the pie
%     diff_to_50 = (0.5*pie_size - offer);
%     
%     %Mean deviation from 100% of the pie
%     diff_to_100 = (pie_size - offer);
% end
% 
% % USE THE DIVIDE FUNCTION! IT WAS THE ONE USED.
% ds=0.00000001;
% x0=0;
% for cont=1:length(Offer)
%     U = @(d_i) (1) * ((-(1+0.5*d_i)/(s_w(cont) + ds ))-((1-0.5*d_i)/(s_f(cont) + ds )));
% %     U = @(i) (-1) * ((-i(1)/(s_w(cont) + ds ))-(i(2)/(s_f(cont) + ds )));
% %     d_ie(cont)=fmincon(U,x0,[-1; 1],[1 1]);  % AX<=b => -1 <= X <= 1
%     d_ie(cont)=fmincon(U,x0,[-1; 1],[1 1]);  % AX<=b => -1 <= X <= 1
% %     d_ie(cont)=fmincon(U,x0,[-1; 1],[1 1])  % AX<=b => -1 <= X <= 1
%     i_w(cont)= 1 +0.5 * d_ie(cont);
%     i_f(cont)= 1 -0.5 * d_ie(cont);
%     result(cont)=((-i_w(cont)/(s_w(cont)+ds))-(i_f(cont)/(s_f(cont)+ds)));
% end
% 
% %Analyze results
% figure
% plot(result)
% 
% 
% 
% 

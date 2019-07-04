%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Moral analyzer - Ultimatum game
% Luciano C. Siebert; Rijk Mercuur
% TU DELFT
% 06.2019
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clc
clear all
close all
data_simulation=importfile('IndividualResponseAndValueDifference.2019.Jul.03.10_24_48.txt');
% ["tick", "ID", "LastAction", "ValueDifference"]
% - the tick column signifies the round
% - ID is the agent ID obvioulsy
% - LastAction: if it is between 0 and 1000 it specifies the demands if it -1337 it specifies a reject if its 1337 its an accept
% -ValueDifference: if its -200 it means its a normative agent, any other number means it is the actually di for the value-agent


%% Parameters
pie_size=1000;



%% Analyzing
%Estimatino parameters: di (which leads to iw and if)

%Resulting money - r
r = table.amountoffered;

%Mean deviation from 50% of the pie
deviation_from_50 = (50 - table.amountoffered);

%Mean deviation from 100% of the pie
deviation_from_100 = (100 - table.amountoffered);


s_w = r / 1000;
s_f = 1 - (abs(0.5 * pie_size - r) / (0.5 * pie_size));


% USE THE DIVIDE FUNCTION! IT WAS THE ONE USED.
ds=0.00000001;
x0=0;
for cont=1:size(table,1)
    U = @(d_i) (-1) * ((-(1+0.5*d_i)/(s_w(cont) + ds ))-((1-0.5*d_i)/(s_f(cont) + ds )));
%     U = @(i) (-1) * ((-i(1)/(s_w(cont) + ds ))-(i(2)/(s_f(cont) + ds )));
    d_ie(cont)=fmincon(U,x0,[-1; 1],[1 1]);  % AX<=b => -1 <= X <= 1
%     d_ie(cont)=fmincon(U,x0,[-1; 1],[1 1])  % AX<=b => -1 <= X <= 1
    i_w(cont)= 1 +0.5 * d_ie(cont);
    i_f(cont)= 1 -0.5 * d_ie(cont);
    result(cont)=((-i_w(cont)/(s_w(cont)+ds))-(i_f(cont)/(s_f(cont)+ds)));
end

%Analyze results
figure
plot(result)

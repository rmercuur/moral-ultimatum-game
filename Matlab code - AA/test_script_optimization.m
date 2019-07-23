%% TEST SCRIPT

clear all
clc
close all

pie_size=1000;
d_s=0.000000001;


for offer = 1:1000
    
    % i_w = 1 + 0.5 *d_i;
    % i_f = 1 - 0.5 * d_i;
    s_w = offer / 1000;
    s_f = 1 - (abs(0.5 * pie_size - offer) / (0.5 * pie_size));
    % u(offer) = ((-(i_w)/(s_w + d_s ))-((i_f)/(s_f + d_s )));
    
    %CONSTRAIN
    
    % plot(u)
    % [M,I]=max(u)
    % hold on; % hold the plot for other curves
    % plot(I,M,'o','MarkerSize',10);
    % text(I-500,M-50,strcat('Offer:',num2str(I),' U:',num2str(M),' Di:',num2str(d_i)))
    fmin=@(x)(-1)*((-(1+0.5*x)/(s_w+d_s))-((1-0.5*x)/(s_f+d_s)));
    x0=0;
    lb=-2;
    ub=2;
    d_i(offer)=optimtest(fmin,x0,lb,ub);
end
plot(d_i)

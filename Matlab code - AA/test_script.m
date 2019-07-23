%% TEST SCRIPT

clear all
clc
close all

pie_size=1000;
d_s=0.5;
for offer =1:1000
d_i = 1;
i_w = 1 + 0.5 *d_i;
i_f = 1 - 0.5 * d_i;
s_w = offer / 1000;
s_f = 1 - (abs(0.5 * pie_size - offer) / (0.5 * pie_size));
u(offer) = ((-(i_w)/(s_w + d_s ))-((i_f)/(s_f + d_s )));
end

plot(u)
[M,I]=max(u)
hold on; % hold the plot for other curves
plot(I,M,'o','MarkerSize',10);
text(I,M,strcat('Offer:',num2str(I),' U:',num2str(M),' Di:',num2str(d_i)))




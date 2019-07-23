function [x,fval,exitflag,output] = test(x0)
%% This is an auto generated MATLAB file from Optimization Tool.

%% Start with the default options
options = optimset;
%% Modify options setting
options = optimset(options,'Display', 'iter');
options = optimset(options,'PlotFcns', {  @optimplotx @optimplotfunccount @optimplotfval });
[x,fval,exitflag,output] = ...
fminsearch(@(x)(-1)*x(1)*s_w(1)+x(2)*s_f(1),x0,options);

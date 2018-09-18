<#macro seplist range sep="">
<#list range as x><#if x != range?first>${sep}</#if><#nested x></#list></#macro>
<#macro csv range><@seplist range=range sep=", "; x><#nested x></@seplist></#macro>
<#macro csvNL range><@seplist range=range sep=",\n"; x><#nested x></@seplist></#macro>
<#macro lines range><@seplist range=range sep="\n"; x><#nested x></@seplist></#macro>
<#macro typeParameters arity><@csv 1..arity; j>T${j}</@csv></#macro>
<#macro parameters arity><@csv 1..arity; j>final T${j} t${j}</@csv></#macro>
<#macro repeat range><@seplist range=range sep=""; x><#nested x></@seplist></#macro>
<#macro tupleArgs arity><@csv 1..arity; j>tuple._${j}()</@csv></#macro>
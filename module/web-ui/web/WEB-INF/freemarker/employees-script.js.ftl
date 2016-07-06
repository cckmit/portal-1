var CaughtLetters = ["А","Б","В","Г","Д","Е","Ж","З","И","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ш","Щ","Ю","Я"];
var Employees = [
<#list employees as e>{id  : ${e.id}, fio : "${e.displayName}", birthday: "<#if e.birthday??>${e.birthday?string["MMMM, dd"]}</#if>", post : "${e.displayPosition!}"}<#sep>,</#sep></#list>
];
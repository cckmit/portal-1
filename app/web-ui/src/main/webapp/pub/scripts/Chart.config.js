var options = {
    segmentStrokeWidth : 1,
    animateRotate : false
};
Chart.defaults.global.tooltipTemplate = "<%if (label){%><%=label%><%}%>:<%=value%>";
Chart.defaults.global.tooltipCornerRadius = 0;
Chart.defaults.global.tooltipFontStyle = "bold";
Chart.defaults.global.tooltipFontFamily = "'Arial', 'Helvetica', 'sans-serif'";
Chart.defaults.global.tooltipFontSize = 12;
Chart.defaults.global.tooltipFontColor = "#DAE0EA";
Chart.defaults.global.tooltipFillColor = "#506177";

function generateChartjsTooltip(tooltipEl, tooltip) {

    if (!tooltip) {
        tooltipEl.css({
            display: 'none'
        });
        return;
    }

    tooltipEl.addClass(tooltip.yAlign);

    var parts = tooltip.text.split(":");
    var time = parts[1].split(".");

    if(time[0].length<2)time[0] = '0'+time[0].trim();

    if(!time[1])time[1] = ':00';
    else if(time[1].length==1)time[1] = ':0'+time[1].trim();
    else time[1] = ':'+time[1].trim();

    var innerHtml = '<span>' + parts[0].trim() + '</span>: <span><b>' + time[0] + time[1] + '</b></span>';
    tooltipEl.html(innerHtml);

    tooltipEl.css({
        display: 'block',
        left: tooltip.chart.canvas.offsetLeft + tooltip.x + 'px',
        top: tooltip.chart.canvas.offsetTop + tooltip.y + 'px',
        fontFamily: tooltip.fontFamily,
        fontSize: tooltip.fontSize,
        fontStyle: tooltip.fontStyle
    });
}
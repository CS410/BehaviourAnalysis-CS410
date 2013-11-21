var WIDTH = window.innerWidth,
    HEIGHT = window.innerHeight,
    RADIUS = Math.min(WIDTH, HEIGHT) / 2.5; // Scaling the radius 

var DAY_DURATION = 5000,
	DAY_FREQUENCY = 500,
	DAY_INDEX = 0;

var	z = d3.scale.category20c(); // MAGIC?

// Our base svg element that contains all the graphics
var svg = d3.select("body").append("svg")
	.attr("width", WIDTH)
    .attr("height", HEIGHT);

//////////////////////////////////////////////////////////////////////////
// TEST DATA
//////////////////////////////////////////////////////////////////////////

var array_fallback = [	[0, 20, 40, 50, 55, 57, 60, 1000],
						[40, 78, 1000],
						[1000],
						[2, 12, 40, 44, 330],
						[1000],
						[1000],
						[10,20,30,40,50,60,70,80] ]
var fallback_DAY_INDEX = 0;
var array = array_fallback[fallback_DAY_INDEX].slice(0);	

//////////////////////////////////////////////////////////////////////////
// VISUALIZATION BEGIN
//////////////////////////////////////////////////////////////////////////	

// Setup clockhand once, through enter	
svg.selectAll("path").data(getMillisecond)
	.enter().append("path")
	.style("fill", "none")
	.attr("d", arc)
	.attr("id", "clockhand")
	.attr("stroke", "black")
	.attr("stroke-width", 5)
	.attr("transform", "translate(" + WIDTH / 2 + "," + HEIGHT / 2 + ")");

// Setup outside border circle
svg.append("svg:circle")
	.attr("id", "daycircle-border")
	.attr("r", RADIUS)
	.attr("cx", WIDTH / 2)
	.attr("cy", HEIGHT / 2)
	.attr("stroke-width", 5)
	.attr("stroke", z)
	.attr("fill", "none");

	
//////////////////////////////////////////////////////////////////////////
// START TIMERS
//////////////////////////////////////////////////////////////////////////	
drawCircle(); // Draw initial circle

// New circle every DAY_FREQUENCY - same as rotating clock hand
setInterval(function() {
	fallback_DAY_INDEX = (fallback_DAY_INDEX+1)%7;
	array = array_fallback[fallback_DAY_INDEX].slice(0);
	drawCircle();
}, DAY_FREQUENCY);

var START_TIME = (new Date).getTime();
var t0 = window.performance.now();
// Timer is not guaranteed to tick 1 ms, accuracy is up to 10ms
d3.timer(function() {
	var datams = getNow();
	var clockhand = svg.select("#clockhand").data(datams);
	// Update clockhand
	clockhand.attr("d", arc)
		.attr("stroke", "black")
		.attr("stroke-width", 5);
	
	if (datams[0].value%DAY_FREQUENCY >= array[0]) {
		plotCommit(DAY_INDEX, array[0]);
		array.shift();
	}
});

//////////////////////////////////////////////////////////////////////////
// DRAWING/UPDATING FUNCTIONS
//////////////////////////////////////////////////////////////////////////

// Draws a day circle
function drawCircle() {
	DAY_INDEX++;
	svg.append("svg:circle")
		.attr("id", "circle-".concat(DAY_INDEX.toString()))
		.attr("cx", WIDTH / 2)
		.attr("cy", HEIGHT / 2)
		.attr("r", RADIUS)
		.style("stroke", z)
		.style("stroke-opacity", 1)
		.style("fill", "none")
		.transition()
			.duration(DAY_DURATION)
			.ease("linear")	
			.attr("r", 1)
			.remove();

	var dayselector = "#circle-".concat(DAY_INDEX.toString());	  
	var circle = d3.select(dayselector);		
	return circle[0];
}

// Plots commit circle onto day circle
function plotCommit(circleDAY_INDEX, ms) {
	var dayselector = "#circle-".concat(circleDAY_INDEX.toString());	  
	var circle = svg.select(dayselector);
	var radius = circle.attr("r");
	svg.append("svg:circle")
		.attr("cx", xpoint(ms, radius))
		.attr("cy", ypoint(ms, radius))
		.attr("r", 5)
		.style("stroke", "red")
		.style("stroke-opacity", 1)
		.style("fill", "black")
		.transition()
			.duration(scaleDur(circle.attr("r")))
			.ease("linear")
			.attr("cx", WIDTH/2)
			.attr("cy", HEIGHT/2)
			.remove();
}

//////////////////////////////////////////////////////////////////////////
// UTILITY FUNCTIONS
//////////////////////////////////////////////////////////////////////////

// Scaling functions for seconds and duration plotting
var scaleSecs = d3.scale.linear().domain([0, DAY_FREQUENCY-1]).range([0, 2 * Math.PI]);
var scaleDur  = d3.scale.linear().domain([1, RADIUS]).range([0, DAY_DURATION]);

// Arc function to update clock hand
var arc = d3.svg.arc()
    .startAngle(function(d) { return scaleSecs(d.value); })
    .endAngle(function(d) { return scaleSecs(d.value); })
    .innerRadius(0)
    .outerRadius(RADIUS);

// Returns milliseconds since start
function getMillisecond() {
	var epoch = (new Date).getTime();
	return [
		{value: epoch - START_TIME}
	];
}

// Returns window.performance.now, more accurate than new Date
function getNow() {
	var t = window.performance.now();
	return [
		{ value: t - t0 }
	];
}

// Converts ms to angle and generates x point to plot
function xpoint(ms, radius) {
	var angle = scaleSecs(ms);
	return WIDTH/2 + Math.sin(angle) * radius
}

// Converts ms to angle and generates y point to plot
function ypoint(ms, radius) {
	var angle = scaleSecs(ms);
	return HEIGHT/2 - Math.cos(scaleSecs(ms)) * radius
}


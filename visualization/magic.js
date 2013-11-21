var WIDTH = window.innerWidth,
    HEIGHT = window.innerHeight,
    RADIUS = Math.min(WIDTH, HEIGHT) / 2.5; // Scaling the radius 

var DAY_DURATION = 20000,
	DAY_FREQUENCY = 500,
	DAY_INDEX = 0;

var	z = d3.scale.category20c(); // MAGIC?

var COLOURS = [	d3.rgb(255,0,0),
				d3.rgb(0,0,255),
				d3.rgb(255,255,0),
				d3.rgb(0,255,0),
				d3.rgb(128,0,128),
				d3.rgb(255,165,0),
				d3.rgb(128,128,128) ]

var RED 	= 0,
	BLUE	= 1,
	YELLOW 	= 2,
	GREEN	= 3,
	PURPLE	= 4,
	ORANGE	= 5,
	GRAY	= 6;


// Our base svg element that contains all the graphics
var svg = d3.select("body").append("svg")
	.attr("width", WIDTH)
    .attr("height", HEIGHT);

//////////////////////////////////////////////////////////////////////////
// TEST DATA
//////////////////////////////////////////////////////////////////////////

var arrayfallback = [	[0, 20, 40, 50, 55, 57, 60, 1000],
						[40, 78, 1000],
						[1000],
						[2, 12, 40, 44, 330],
						[1000],
						[1000],
						[10,20,30,40,50,60,70,80] ]
var fallbackindex = 0;
var array = arrayfallback[fallbackindex].slice(0);	

var timepiesum = 0;
var timepieindex = 0;

//////////////////////////////////////////////////////////////////////////
// VISUALIZATION BEGIN
//////////////////////////////////////////////////////////////////////////	

// Setup clockhand once, through enter	

svg.selectAll("#timepie").data(getNow)
	.enter().append("path")
	.attr("d", timepiearc)
	.attr("opacity", 0.3)
	.attr("id", "timepie")
	.attr("stroke", "black")
	.attr("stroke-width", 0)
	.attr("fill", "none")

	.attr("transform", "translate(" + WIDTH / 2 + "," + HEIGHT / 2 + ")");

svg.selectAll("#clockhand").data(getNow)
	.enter().append("path")
	.style("fill", "black")
	.attr("d", clockarc)
	.attr("id", "clockhand")
	.attr("stroke", "black")
	.attr("stroke-width", 0)
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
	
svg.append("svg:circle")
	.attr("id", "commitcircle")
	.attr("r", 0)
	.attr("cx", WIDTH/2)
	.attr("cy", HEIGHT/2)
	.attr("stroke-width", 1)
	.attr("opacity", 0.3)
	.attr("fill", "none");
	
//////////////////////////////////////////////////////////////////////////
// START TIMERS
//////////////////////////////////////////////////////////////////////////	
drawCircle(); // Draw initial circle

// New circle every DAY_FREQUENCY - same as rotating clock hand
setInterval(function() {
	fallbackindex = (fallbackindex+1)%7;
	array = arrayfallback[fallbackindex].slice(0);
	drawCircle();
}, DAY_FREQUENCY);

var START_TIME = (new Date).getTime();
var t0 = window.performance.now();
// Timer is not guaranteed to tick 1 ms, accuracy is up to 10ms
d3.timer(function() {
	var dataclock = getNow();
	var clockhand = svg.select("#clockhand").data(dataclock);
	// Update clockhand
	clockhand.attr("d", clockarc)
		.attr("stroke", "black")
		.attr("stroke-width", 5);
	
	if (dataclock[0].value%DAY_FREQUENCY >= array[0]) {
		plotCommit(DAY_INDEX, array[0]);
		
		var datacommit = getCirclerad();
		var commitcircle = svg.select("#commitcircle")
			.transition()
			.duration(500)
			.attr("r", datacommit[0].radius)
			.attr("fill", datacommit[0].colour);
		
		var datapie = getTimepie(array[0]);
		var timepie = svg.select("#timepie").data(datapie);
		timepie.transition()
			.duration(500)
			.attr("d", timepiearc)
			.attr("fill", datacommit[0].colour)
			.attr("stroke", "black")
			.attr("stroke-width", 0);
		

		
		
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
function plotCommit(circleindex, ms) {
	var dayselector = "#circle-".concat(circleindex.toString());	  
	var circle = svg.select(dayselector);
	var radius = circle.attr("r");
	svg.append("svg:circle")
		.attr("cx", xpoint(ms, radius))
		.attr("cy", ypoint(ms, radius))
		.attr("r", randomradius)
		.style("stroke", "none")
		.style("stroke-opacity", 1)
		.style("fill", colourrandom())
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
var clockarc = d3.svg.arc()
    .startAngle(function(d) { return scaleSecs(d.value); })
    .endAngle(function(d) { return scaleSecs(d.value); })
    .innerRadius(0)
    .outerRadius(RADIUS);

var timepiearc = d3.svg.arc()
    .startAngle(function(d) { return scaleSecs(d.start); })
    .endAngle(function(d) { return scaleSecs(d.end); })
	.innerRadius(0)
	.outerRadius(RADIUS+50);
	
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
function getTimepie(time) {
	timepieindex++;
	timepiesum = timepiesum + time;
	
	return [
		{ start: (timepiesum/timepieindex)-20,
		  end: 	(timepiesum/timepieindex)+20 }	
	]
}

function getCirclerad() {
	return [
		{ radius: Math.floor(Math.random()*RADIUS),
		  colour: d3.rgb(Math.floor(Math.random()*255),	Math.floor(Math.random()*255), Math.floor(Math.random()*255))
		}
	]
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

function colourrandom() {
	return d3.rgb(Math.floor(Math.random()*255),	Math.floor(Math.random()*255), Math.floor(Math.random()*255));
}

function randomradius() {
	return Math.max(5, Math.floor(Math.random()*20));
}

function colour(m, v, c) {
	if (m >= 70)
		return RED;
	else if (v >= 70)
		return YELLOW;
	else if (c >= 70)
		return BLUE;
	else if ((m+v) >= 70)
		return ORANGE;
	else if ((m+c) >= 70)
		return PURPLE;
	else if ((v+c) >= 70)
		return GREEN;
	else 
		return GRAY;
}




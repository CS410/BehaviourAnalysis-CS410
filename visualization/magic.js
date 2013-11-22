var developerkey = "kemitche";

var WIDTH = window.innerWidth,
    HEIGHT = window.innerHeight,
	OUTER = 75,
    RADIUS = Math.min(WIDTH, HEIGHT) / 3; // Scaling the radius 

	
var DAY_DURATION = 10000,
	DAY_FREQUENCY = 250,
	DAY_INDEX = -1;

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
// DATA
//////////////////////////////////////////////////////////////////////////

var commitlist = jsondata[developerkey].commitList;
var sortedkeys = Object.keys(commitlist).sort();
var linesmodified = 0;
var mlines = 0;
var vlines = 0;
var clines = 0;

var colourdata = [[0,0,COLOURS[RED]], [0,0,COLOURS[BLUE]], [0,0,COLOURS[YELLOW]], [0,0,COLOURS[GRAY]]];


//////////////////////////////////////////////////////////////////////////
// VISUALIZATION BEGIN
//////////////////////////////////////////////////////////////////////////	

svg.selectAll(".mvcpie").data(colourdata)
	.enter().append("path")
	.attr("d", mvcpiearc)
	.attr("class", "mvcpie")
	.attr("fill", function(d){return d[2];})
	.attr("opacity", 0.25)
	.attr("transform", "translate("+WIDTH/2+","+HEIGHT/2+")");

svg.selectAll("#timewedge").data(getNow)
	.enter().append("path")
	.attr("id", "timewedge")
	.attr("d", timewedgearc)
	.attr("opacity", 0.5)
	.attr("stroke", "black")
	.attr("stroke-width", 0)
	.attr("fill", "none")
	.attr("transform", "translate(" + WIDTH / 2 + "," + HEIGHT / 2 + ")");

svg.selectAll("#clockhand").data(getNow)
	.enter().append("path")
	.attr("id", "clockhand")
	.attr("d", clockarc)
	.attr("stroke", "black")
	.attr("stroke-width", 0)
	.attr("fill", "black")
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
	.attr("opacity", 0.5)
	.attr("fill", "none");
	
//////////////////////////////////////////////////////////////////////////
// START TIMERS
//////////////////////////////////////////////////////////////////////////	
drawCircle(); // Draw initial circle

// New circle every DAY_FREQUENCY - same as rotating clock hand
setInterval(function() {
	//console.log(commitlist[sortedkeys[DAY_INDEX]]);
	drawCircle();
}, DAY_FREQUENCY);

var t0 = window.performance.now();

// Timer is not guaranteed to tick 1 ms, accuracy is up to 10ms
d3.timer(function() {
	var dataclock = getNow();
	var clockhand = svg.select("#clockhand").data(dataclock);
	// Update clockhand
	clockhand.attr("d", clockarc)
		.attr("stroke", "black")
		.attr("stroke-width", 5);
	
	var commits = commitlist[sortedkeys[DAY_INDEX]];
	if (commits.length > 0) {
		var commit = commits[0];
		var ms = scaleSeconds(commit.authoredDateInSeconds);
		if (dataclock[0].value%DAY_FREQUENCY >= ms) {
			//console.log(dataclock[0].value%500 + " vs " + array[0]);
			plotCommit(DAY_INDEX, commit);
			updateBackground(commit);
			updateWedge(commit);
			commits.shift();
		}
	}
}, 1);

//////////////////////////////////////////////////////////////////////////
// DRAWING/UPDATING FUNCTIONS
//////////////////////////////////////////////////////////////////////////

// Draws a day circle
function drawCircle() {
	DAY_INDEX++;

	//console.log(sortedkeys[DAY_INDEX]);
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
function plotCommit(circleindex, commit) {
	var dayselector = "#circle-".concat(circleindex.toString());	  
	var circle = svg.select(dayselector);
	var radius = circle.attr("r");
	
	var committime = scaleSeconds(commit.authoredDateInSeconds);
	var commitradius = commit.additions + commit.deletions;
	var commitcolour = colour(	commit.modelLines, 
								commit.viewLines, 
								commit.controllerLines, 
								commit.additions + commit.deletions	);
	
	svg.append("svg:circle")
		.attr("cx", xpoint(committime, radius))
		.attr("cy", ypoint(committime, radius))
		.attr("r", commitradius)
		.style("stroke", commitcolour.darker(2))
		.style("stroke-width", 2)
		.style("fill", commitcolour)
		.transition()
			.duration(scaleDuration(circle.attr("r")))
			.ease("linear")
			.attr("cx", WIDTH/2)
			.attr("cy", HEIGHT/2)
			.remove();
}

function updateWedge(commit) {
	var datapie = getTimewedge(commit.timeAverage, commit.timeStdev);
	var timewedge = svg.select("#timewedge").data(datapie);
	timewedge.transition()
		.duration(500)
		.attr("d", timewedgearc)
		.attr("fill", d3.select("#commitcircle").attr("fill"))
		.attr("stroke", "black")
		.attr("stroke-width", 1);
}

function updateBackground(commit) {
	mlines = commit.modelLines + mlines;
	vlines = commit.viewLines + vlines;
	clines = commit.controllerLines + clines;
	linesmodified = commit.additions + commit.deletions + linesmodified;
	
	//console.log(mlines + " " + vlines + " " + clines + " " + linesmodified);
	
	var commitcircle = svg.select("#commitcircle")
		.transition()
		.duration(500)
		.attr("r", commit.average)//s + commit.standardDeviation)
		.attr("fill", colour(mlines, vlines, clines, linesmodified));
		
	var M = Math.floor(100*mlines/linesmodified);
	var V = Math.floor(100*vlines/linesmodified);
	var C = Math.floor(100*clines/linesmodified);
	
	colourdata = [	[0,M,COLOURS[RED]], 
					[M,M+V,COLOURS[YELLOW]], 
					[M+V,M+V+C,COLOURS[BLUE]], 
					[M+V+C,100,COLOURS[GRAY]]];
		
	svg.selectAll(".mvcpie").data(colourdata)
		.transition()
		.duration(500)
		.attr("d", mvcpiearc);
		
	//console.log(commit.average + " " + commit.standardDeviation);
}

//////////////////////////////////////////////////////////////////////////
// UTILITY FUNCTIONS
//////////////////////////////////////////////////////////////////////////

// Scaling functions for seconds and duration plotting
var scaleClock = d3.scale.linear().domain([0, DAY_FREQUENCY-1]).range([0, 2*Math.PI]);
var scaleDuration  = d3.scale.linear().domain([1, RADIUS]).range([0, DAY_DURATION]);
var scaleMVC = d3.scale.linear().domain([0,100]).range([0, 2*Math.PI]);
var scaleSeconds = d3.scale.linear().domain([0,86400]).range([0, DAY_FREQUENCY-1]);
var scaleHours = d3.scale.linear().domain([0,23]).range([0, DAY_FREQUENCY-1]);

// Arc function to update clock hand
var clockarc = d3.svg.arc()
    .startAngle(function(d) { return scaleClock(d.value); })
    .endAngle(function(d) { return scaleClock(d.value); })
    .innerRadius(0)
    .outerRadius(RADIUS);

var timewedgearc = d3.svg.arc()
    .startAngle(function(d) { return scaleClock(d.start); })
    .endAngle(function(d) { return scaleClock(d.end); })
	.innerRadius(0)
	.outerRadius(RADIUS+OUTER);
		
var mvcpiearc = d3.svg.arc()
	.innerRadius(RADIUS+OUTER)
	.outerRadius(RADIUS+(OUTER*2))
	.startAngle(function(d){return scaleMVC(d[0]);})
	.endAngle(function(d){return scaleMVC(d[1]);});

// Returns window.performance.now, more accurate than new Date
function getNow() {
	var t = window.performance.now();
	return [
		{ value: t - t0 }
	];
}
function getTimewedge(time, std) {
	return [
		{ start: scaleHours(time-std),
		  end: 	scaleHours(time+std) }	
	]
}

// Converts ms to angle and generates x point to plot
function xpoint(ms, radius) {
	var angle = scaleClock(ms);
	return WIDTH/2 + Math.sin(angle) * radius
}

// Converts ms to angle and generates y point to plot
function ypoint(ms, radius) {
	var angle = scaleClock(ms);
	return HEIGHT/2 - Math.cos(scaleClock(ms)) * radius
}

function colour(m, v, c, total) {
	if (m/total >= 0.60)
		return COLOURS[RED];
	else if (v/total >= 0.60)
		return COLOURS[YELLOW];
	else if (c/total >= 0.60)
		return COLOURS[BLUE];
	else if ((m+v)/total >= 0.70)
		return COLOURS[ORANGE];
	else if ((m+c)/total >= 0.70)
		return COLOURS[PURPLE];
	else if ((v+c)/total >= 0.70)
		return COLOURS[GREEN];
	else 
		return COLOURS[GRAY];
}




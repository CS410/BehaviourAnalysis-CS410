var developerkey = "kemitche";

var dev = document.getElementById("dev");
dev.innerHTML = "<b>"+developerkey+"</b>";

var datediv = document.getElementById("date");


var WIDTH = window.innerWidth,
    HEIGHT = window.innerHeight,
	OUTER = 75,
    RADIUS = Math.min(WIDTH, HEIGHT) / 3; // Scaling the radius 
var MULTIPLIER = 10;
	
var DAY_DURATION = 2500,
	DAY_FREQUENCY = DAY_DURATION / MULTIPLIER,
	DAY_INDEX = -1;
	
var TRANSITION = Math.min(1000, DAY_DURATION/2);

var	z = d3.scale.category20c(); // MAGIC?

var COLOURS = [	d3.rgb(255,0,0),
				d3.rgb(0,0,255),
				d3.rgb(255,255,0),
				d3.rgb(0,255,0),
				d3.rgb(128,0,128),
				d3.rgb(255,128,0),
				d3.rgb(128,128,128),
				d3.rgb(102,255,255)]

var RED 	= 0,
	BLUE	= 1,
	YELLOW 	= 2,
	GREEN	= 3,
	PURPLE	= 4,
	ORANGE	= 5,
	GRAY	= 6,
	CYAN 	= 7;


// Our base svg element that contains all the graphics
var svg = d3.select("#svgdiv").append("svg")
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
var llines = 0;

var mvclines = 0;

var Ncommit = 0;
var S2commit = 0;
var S1commit = 0;

var Nhour = 0;
var S2hour = 0;
var S1hour = 0;

var colourdata = getMVCpie(0,0,0,0,[0,0,0,0]);

// Scaling functions for seconds and duration plotting
var scaleClock = d3.scale.linear().domain([0, DAY_FREQUENCY-1]).range([0, 2*Math.PI]);
var scaleDuration  = d3.scale.linear().domain([1, RADIUS]).range([0, DAY_DURATION]);
var scaleMVC = d3.scale.linear().domain([0,100]).range([0, 2*Math.PI]);
var scaleSeconds = d3.scale.linear().domain([0,86400]).range([0, DAY_FREQUENCY-1]);
var scaleHours = d3.scale.linear().domain([0,23]).range([0, DAY_FREQUENCY-1]);
//var scaleDiff = d3.scale.linear().domain([0,100]).range([0, 500]);
var scaleSquare = d3.scale.pow().exponent(2);

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
	.startAngle(function(d){return scaleMVC(d.start);})
	.endAngle(function(d){return scaleMVC(d.end);})	
	.innerRadius(RADIUS+OUTER)
	.outerRadius(RADIUS+(OUTER*2));


//////////////////////////////////////////////////////////////////////////
// VISUALIZATION BEGIN
//////////////////////////////////////////////////////////////////////////	

//svg.selectAll(".mvcpie").data(colourdata)
//	.enter().append("path")
//	//.attr("d", mvcpiearc)
//	.attr("class", "mvcpie")
//	.attr("fill", function(d){return d.colour;})
//	.attr("opacity", 0.25)
//	.attr("transform", "translate("+WIDTH/2+","+HEIGHT/2+")");

svg.selectAll("#timewedge").data(getTimewedge)
	.enter().append("path")
	.attr("id", "timewedge")
	//.attr("d", timewedgearc)
	.attr("opacity", 0.5)
	.attr("stroke", "black")
	.attr("stroke-width", 0)
	.attr("fill", "none")
	.attr("transform", "translate(" + WIDTH / 2 + "," + HEIGHT / 2 + ")");

svg.selectAll("#clockhand").data(getNow)
	.enter().append("path")
	.attr("id", "clockhand")
	//.attr("d", clockarc)
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
	.attr("stroke-width", 2)
	.attr("stroke", "black")
	.attr("opacity", 0.5)
	.attr("fill", "none");
	
svg.append("svg:circle")
	.attr("id", "commitcircle-std")
	.attr("r", 0)
	.attr("cx", WIDTH/2)
	.attr("cy", HEIGHT/2)
	.attr("stroke-width", 0)
	.attr("opacity", 0.25)
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
		var ms = scaleSeconds(commit.committedDateInSeconds);
		if (dataclock[0].value%DAY_FREQUENCY >= ms) {
			
			mlines = commit.modelLines + mlines;
			vlines = commit.viewLines + vlines;
			clines = commit.controllerLines + clines;
			llines = commit.libLines + llines;
			linesmodified = commit.additions + commit.deletions + linesmodified;
			mvclines = mlines + vlines + clines;
		
			//console.log(dataclock[0].value%500 + " vs " + array[0]);
			plotCommit(DAY_INDEX, commit);
			//updateBackground(commit);
			//updateWedge(commit);
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
	datediv.innerHTML = "<b>"+sortedkeys[DAY_INDEX]+"</b>";
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

function update(commit) {
	updateBackground(commit);
	updateWedge(commit);
	console.log(mlines + " " + vlines + " " + clines + " " + llines + " " + mvclines);
}

// Plots commit circle onto day circle
function plotCommit(circleindex, commit) {
	var dayselector = "#circle-".concat(circleindex.toString());	  
	var circle = svg.select(dayselector);
	var radius = circle.attr("r");
	
	var committime = scaleSeconds(commit.committedDateInSeconds);
	var commitradius = commit.additions + commit.deletions;
	var commitcolour = colour(	commit.modelLines, 
								commit.viewLines, 
								commit.controllerLines, 
								commit.libLines,
								commit.additions + commit.deletions	);
	
	var plottedCommit = svg.append("svg:circle")
		.attr("cx", xpoint(committime, radius))
		.attr("cy", ypoint(committime, radius))
		.attr("r", Math.min(Math.max(5,commitradius), RADIUS/2))
		.style("stroke", commitcolour.darker(2))
		.style("stroke-width", 2)
		.style("fill", commitcolour)
		.attr("opacity", 0.5)
		.transition()
			.duration(scaleDuration(circle.attr("r")))
			.ease("linear")
			.attr("cx", WIDTH/2)
			.attr("cy", HEIGHT/2)
			.each("end", function() { update(commit); })
			.remove();
			
	//plottedCommit.each("end", console.log("destroyed"));
}


function updateWedge(commit) {
	Nhour++;
	S1hour = S1hour + Math.floor(commit.committedDateInSeconds / 3600);
	S2hour = S2hour + Math.pow(Math.floor(commit.committedDateInSeconds / 3600), 2);

	var datapie = getTimewedge(S1hour/Nhour, std(Nhour,S2hour,S1hour));
	var timewedge = svg.select("#timewedge").data(datapie);
	timewedge.transition()
		.duration(TRANSITION)
		.attr("d", timewedgearc)
		.attr("fill", colour(mlines, vlines, clines, llines, mvclines))
}

function updateBackground(commit) {
				
	Ncommit++;
	S1commit = S1commit + commit.additions + commit.deletions;
	S2commit = S2commit + scaleSquare(commit.additions + commit.deletions);
	
	var commitcircle = svg.select("#commitcircle")
		.transition()
		.duration(TRANSITION)
		.attr("r", Math.min(Math.min(WIDTH, HEIGHT), Math.max(5,S1commit/Ncommit)))
		.attr("fill", colour(mlines, vlines, clines, llines, mvclines));
		
	var commitcirclestd = svg.select("#commitcircle-std")
		.transition()
		.duration(TRANSITION)
		.attr("r", Math.min(Math.min(WIDTH, HEIGHT), 
							Math.max(5,S1commit/Ncommit + std(Ncommit,S2commit,S1commit))))
		.attr("fill", colour(mlines, vlines, clines, llines, mvclines));
/*
	var M = Math.floor(100*(mlines/linesmodified));
	var V = Math.floor(100*(vlines/linesmodified));
	var C = Math.floor(100*(clines/linesmodified));
	var O = 100 - M - V - C;

	var oldM = colourdata[0].end;
	var oldV = colourdata[1].end;
	var oldC = colourdata[2].end;
	var oldO = 100 - oldM - oldV - oldC;
	
	var diff = [Math.abs(M-oldM),
				Math.abs(V-oldV),				
				Math.abs(C-oldC),				
				Math.abs(O-oldO)]
	
	colourdata = getMVCpie(M,V,C,100-M-V-C,diff);
	
	svg.selectAll(".mvcpie").data(colourdata)
		.transition()
		.duration(function(d) { return scaleDiff(d.diff); })
		.attr("d", mvcpiearc);
*/	
}

//////////////////////////////////////////////////////////////////////////
// UTILITY FUNCTIONS
//////////////////////////////////////////////////////////////////////////

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

function getMVCpie(M,V,C,O,diff) {
	return [
			{
				start: 	0,
				end:	M,
				diff: 	diff[0],
				colour:	COLOURS[RED]
			},
			{
				start: 	M,
				end:	M+V, 
				diff: 	diff[1],
				colour:	COLOURS[YELLOW]
			},
			{
				start: 	M+V,
				end:	M+V+C, 
				diff: 	diff[2],
				colour:	COLOURS[BLUE]
			},			
			{
				start: 	M+V+C,
				end:	100, 
				diff: 	diff[3],
				colour:	COLOURS[GRAY]
			},
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

function colour(m, v, c, l, total) {
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
	else if (l/total >= 0.6)
		return COLOURS[CYAN];
	else
		return COLOURS[GRAY];
}

function std(N, S2, S1) {
	return Math.sqrt((N*S2) - Math.pow(S1,2))/N
}



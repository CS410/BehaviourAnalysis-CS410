var WIDTH = window.innerWidth,
    HEIGHT = window.innerHeight,
    RADIUS = Math.min(WIDTH, HEIGHT) / 2.1; // Scaling the radius 

var START_TIME = (new Date).getTime();

// Our base svg element
var svg = d3.select("body").append("svg")
	.attr("width", WIDTH)
    .attr("height", HEIGHT);

// CLOCK HAND	
	
// Maps milliseconds to circle degrees
var scaleSecs = d3.scale.linear().domain([0, 499]).range([0, 2 * Math.PI]);
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


// DAY CIRCLE

var i = 0;
var	z = d3.scale.category20c(); // MAGIC?

function drawCircle() {
	i++;
	svg.append("svg:circle")
		.attr("id", "circle-".concat(i.toString()))
		.attr("cx", WIDTH / 2)
		.attr("cy", HEIGHT / 2)
		.attr("r", RADIUS)
		.style("stroke", z)
		.style("stroke-opacity", 1)
		.style("fill", "none")
		.transition()
			.duration(10000)
			.ease(Math.sqrt)	
			.attr("r", 1)
			.remove();

	var dayselector = "#circle-".concat(i.toString());	  
	var circle = d3.select(dayselector);		
	return circle[0];
}

// START VISUALIZATION

// Setup clockhand once, through enter	
svg.selectAll("path").data(getMillisecond)
	.enter().append("path")
	.style("fill", "none")
	.attr("d", arc)
	.attr("id", "clockhand")
	.attr("stroke", "black")
	.attr("stroke-width", 5)
	.attr("transform", "translate(" + WIDTH / 2 + "," + HEIGHT / 2 + ")");

svg.append("svg:circle")
	.attr("id", "daycircle-border")
	.attr("r", RADIUS)
	.attr("cx", WIDTH / 2)
	.attr("cy", HEIGHT / 2)
	.attr("stroke-width", 5)
	.attr("stroke", z)
	.attr("fill", "none");
	
// Draw initial circle
drawCircle();
	
// Timer is not guaranteed to tick 1 ms, accuracy is up to 10ms
d3.timer(function() {
	var clockhand = svg.select("#clockhand").data(getMillisecond);
	// Update clockhand
	clockhand.attr("d", arc)
		.attr("stroke", "black")
		.attr("stroke-width", 5);
}, 1);

// New circle every 500ms - same as rotating clock hand
setInterval(drawCircle, 500);
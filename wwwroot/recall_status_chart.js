var svg = d3.select("recall_chart")
	.append("svg")
	.append("g")

svg.append("g")
	.attr("class", "slices");
svg.append("g")
	.attr("class", "labels");
svg.append("g")
	.attr("class", "lines");


var pie = d3.layout.pie()
	.sort(null)
	.value(function(d) {
		return d.value;
	});

var arc = d3.svg.arc()
	.outerRadius(radius * 0.8)
	.innerRadius(radius * 0.4);

var outerArc = d3.svg.arc()
	.innerRadius(radius * 0.9)
	.outerRadius(radius * 0.9);

svg.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

var key = function(d){ return d.data.label; };

var color = d3.scale.ordinal()
	.domain(["Ongoing", "Completed","Terminated"])
	.range(["#98abc5","#ff8c00","#a05d56"]);

function combineData(json1,json2,json3){
	var arr = [];
	arr.push(json1.meta.results.total);
	arr.push(json2.meta.results.total);
	arr.push(json3.meta.results.total);
	return arr;
		
}
function randomData (totalcount){
	var labels = color.domain();
	return labels.map(function(label){
		var count = 0;
		if(label == "Ongoing"){count = totalcount[0];}
		else if(label == "Completed"){count = totalcount[1];}
		else if(label == "Terminated"){count = totalcount[2];}
		return { label: label, value:  count}
	});
}

function change(data) {

	/* ------- PIE SLICES -------*/
	var slice = svg.select(".slices").selectAll("path.slice")
		.data(pie(data), key);

	slice.enter()
		.insert("path")
		.style("fill", function(d) { return color(d.data.label); })
		.attr("class", "slice");

	slice		
		.transition().duration(1000)
		.attrTween("d", function(d) {
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(150);
			return function(t) {
				return arc(interpolate(t));
			};
		})

	slice.exit()
		.remove();

	/* ------- TEXT LABELS -------*/

	var text = svg.select(".labels").selectAll("text")
		.data(pie(data), key);

	text.enter()
		.append("text")
		.attr("dy", ".40em")
		.text(function(d) {
			return d.data.label;
		});
	
	function midAngle(d){
		return d.startAngle + (d.endAngle - d.startAngle)/4;
	}

	text.transition().duration(1000)
		.attrTween("transform", function(d) {
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(90);
			return function(t) {
				var d2 = interpolate(t);
				var pos = outerArc.centroid(d2);
				pos[0] = radius * (midAngle(d2) < Math.PI ? 1 : -1);
				return "translate("+ pos +")";
			};
		})
		.styleTween("text-anchor", function(d){
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(90);
			return function(t) {
				var d2 = interpolate(t);
				return midAngle(d2) < Math.PI ? "start":"end";
			};
		});

	text.exit()
		.remove();

	/* ------- SLICE TO TEXT POLYLINES -------*/

	var polyline = svg.select(".lines").selectAll("polyline")
		.data(pie(data), key);
	
	polyline.enter()
		.append("polyline");

	polyline.transition().duration(1000)
		.attrTween("points", function(d){
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
			return function(t) {
				var d2 = interpolate(t);
				var pos = outerArc.centroid(d2);
				pos[0] = radius * 0.80 * (midAngle(d2) < Math.PI ? 1 : -1);
				return [arc.centroid(d2), outerArc.centroid(d2), pos];
			};			
		});
	
	polyline.exit()
		.remove();
};

var class_I = (function(){
			  var class_I = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?search=status:"Ongoing"',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				class_I = data; 
  });
  return class_I;
  })();  
  
var class_II = (function(){
			  var class_II = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?search=status:"Completed"',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				class_II = data; 
  });
  return class_II;
  })();
  
  var class_III = (function(){
			  var class_III = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?search=status:"Terminated"',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				class_III = data; 
  });
  return class_III;
  })();
  var fullArray = combineData(class_I,class_II,class_III);
  console.log("Total number of results for class I " + class_I.meta.results.total);
  console.log("Total number of results for class II " + class_II.meta.results.total);
  console.log("Total number of results for class III " + class_III.meta.results.total);
  change(randomData(fullArray));

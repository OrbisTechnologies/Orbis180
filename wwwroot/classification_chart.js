
var svg = d3.select("#classchart")
	.append("svg")
	.append("g")

svg.append("g")
	.attr("class", "slices");
svg.append("g")
	.attr("class", "labels");
svg.append("g")
	.attr("class", "lines");

var width = d3.select("#classchart").style("width").split("px").shift();
var height = d3.select("#classchart").style("height").split("px").shift();
var 
	radius = Math.min(width, height) / 3;

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
	.domain(["Food", "Devices","Drugs"])
	.range(["#98abc5","#ff8c00","#a05d56"]);

	
function randomData (totalcount){
	var labels = color.domain();
	return labels.map(function(label){
		var count = 0;
		if(label == "Food"){count = totalcount[0];}
		else if(label == "Devices"){count = totalcount[1];}
		else if(label == "Drugs"){count = totalcount[2];}
		return { label: label, value:  count}
	});
}

function combineData(json1,json2,json3){
	var arr = [];
	/*
	for(var i = 0; i < 100; i++)
		arr.push(json1.results[i].product_type);
	for(var j = 0; j < 100; j++)
		arr.push(json2.results[j].product_type);
	for(var k = 0; k < 100; k++)
		arr.push(json3.results[k].product_type);
	return arr;
	*/
	arr.push(json1.meta.results.total);
	arr.push(json2.meta.results.total);
	arr.push(json3.meta.results.total);
	return arr;
		
}


/*
function countClass(jsonData,className){
	var count = 0;
	for(var i = 0; i < 300; i++){
		if(jsonData[i] == className)
			count++;
	}
		return count;
}
*/

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
			this._current = interpolate(0);
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
		.attr("dy", ".35em")
		.text(function(d) {
			return d.data.label;
		});
	
	function midAngle(d){
		return d.startAngle + (d.endAngle - d.startAngle)/2;
	}

	text.transition().duration(1000)
		.attrTween("transform", function(d) {
			this._current = this._current || d;
			var interpolate = d3.interpolate(this._current, d);
			this._current = interpolate(0);
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
			this._current = interpolate(0);
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
				pos[0] = radius * 0.95 * (midAngle(d2) < Math.PI ? 1 : -1);
				return [arc.centroid(d2), outerArc.centroid(d2), pos];
			};			
		});
	
	polyline.exit()
		.remove();
};


var food_json = (function(){
			  var food_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?search=',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				food_json = data; 
  });
  return food_json;
  })();  
  
var device_json = (function(){
			  var device_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/device/enforcement.json?search=',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				device_json = data; 
  });
  return device_json;
  })();
  
  var drug_json = (function(){
			  var drug_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/drug/enforcement.json?search=',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				drug_json = data; 
  });
  return drug_json;
  })();
  console.log("Verify the food type is " + food_json.results[0].product_type);
  console.log("Verify the device type is " + device_json.results[0].product_type);
  console.log("Verify the drug type is " + drug_json.results[0].product_type);
  var fullArray = combineData(food_json,device_json,drug_json);
  console.log("Total number of results in food json " + food_json.meta.results.total);
  console.log("Total number of results in device json " + device_json.meta.results.total);
  console.log("Total number of results in drug json " + drug_json.meta.results.total);
  //var class_one = countClass(fullArray,"Food");
				//console.log(class_one);
				//var class_two = countClass(fullArray,"Devices");
				//console.log(class_two);
				//var class_three = countClass(fullArray,"Drugs");
				//console.log(class_three);
				//var totalcount = [class_one,class_two,class_three]
				change(randomData(fullArray));

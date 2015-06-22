	var food_json = (function(){
			  var food_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?search=report_date:[20040101+TO+20131231]&limit=100',
			  success: function(){console.log("ajax success bubble");},
			  error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
                }}
				).done(function(data) {
				food_json = data; 
  });
  return food_json;
  })(); 
 function countClass(jsonData,className){
	var count = 0;
	for(var i = 0; i < 100; i++){
		var classification = jsonData.results[i].initial_firm_notification;
		if(classification == className)
			count++;
	}
		return count;
}
		//var categories= ['Email','Fax','Letter','Press Release','Telephone','Visit','Multiple'];

		//var dollars = [213,209,190,200,150,140,130];
		var categories = ['E-Mail','Letter', 'Press Release', 'Telephone',/* 'Two or more of the following: Email, Fax, Letter, Press Release, Telephone,'Visit',*/'Other'];
var dollars = [];
	for(var i=0;i<categories.length;i++){
	dollars.push(countClass(food_json,categories[i]));
	
	}
	for(var j=0;j<categories.length;j++)
		console.log(dollars[j]);
	
		var colors = ["#98abc5","#ff8c00","#a05d56",'#99C19E'];

		var grid = d3.range(25).map(function(i){
			return {'x1':0,'y1':0,'x2':0,'y2':400};
		});

		var tickVals = grid.map(function(d,i){
			if(i>0){ return i*7; }
			else if(i===0){ return "100";}
		});

		var xscale = d3.scale.linear()
						.domain([0,30])
						.range([0,700]);

		var yscale = d3.scale.linear()
						.domain([0,categories.length])
						.range([30,325]);

		var colorScale = d3.scale.quantize()
						.domain([0,categories.length])
						.range(colors);

		var canvas = d3.select('#horizontal_barchart')
						.append('svg')
						.attr({'width':775,'height':350});
/*
		var grids = canvas.append('g')
						  .attr('id','grid')
						  .attr('transform','translate(150,10)')
						  .selectAll('line')
						  .data(grid)
						  .enter()
						  .append('line')
						  .attr({'x1':function(d,i){ return i*30; },
								 'y1':function(d){ return d.y1; },
								 'x2':function(d,i){ return i*30; },
								 'y2':function(d){ return d.y2; },
							})
						  .style({'stroke':'#adadad','stroke-width':'1px'});
*/
		var	xAxis = d3.svg.axis();
			xAxis
				.orient('bottom')
				.scale(xscale)
				.tickValues(tickVals);

		var	yAxis = d3.svg.axis();
			yAxis
				.orient('left')
				.scale(yscale)
				.tickSize(3)
				.tickFormat(function(d,i){ return categories[i]; })
				.tickValues(d3.range(25));

		var y_xis = canvas.append('g')
						  .attr("transform", "translate(150,0)")
						  .attr('id','yaxis')
						  .call(yAxis);

		var x_xis = canvas.append('g')
						  .attr("transform", "translate(150,400)")
						  .attr('id','xaxis')
						  .call(xAxis);

		var chart = canvas.append('g')
							.attr("transform", "translate(150,0)")
							.attr('id','bars')
							.selectAll('rect')
							.data(dollars)
							.enter()
							.append('rect')
							.attr('height',30)
							.attr({'x':0,'y':function(d,i){ return yscale(i); }})
							.style('fill',function(d,i){ return colorScale(i); })
							.attr('width',function(d){ return 0; });


		var transit = d3.select("svg").selectAll("rect")
						    .data(dollars)
						    .transition()
						    .duration(1000) 
						    .attr("width", function(d) {return xscale(d); });

		var transitext = d3.select('#bars')
							.selectAll('text')
							.data(dollars)
							.enter()
							.append('text')
							.attr({'x':function(d) {return xscale(d)-250; },'y':function(d,i){ return yscale(i)+25; }})
							.text(function(d){ return d; }).style({'fill':'#fff','font-size':'20px', 'font-family':'calibri'});
//initial_firm_notification

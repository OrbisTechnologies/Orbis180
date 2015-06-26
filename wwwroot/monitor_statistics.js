/*
 *A D3 widget that displays overall statistical data for total recalls, total US *recalls,year to date recalls for the current year as well as the percent change from 
 *the previous year
 * @author Apeenan Arias
 */

/*
 *Method that builds the Widget with the data received from the endpoint 
 *
 */

 var food_json = (function(){
			  var food_json = null;
			  $.ajax({
			  'async': false,
	          url: 'http://localhost:8080/dataIngestion/rest/monitor/queryCount' }
				).done(function(data) {
				food_json = data; 
  });
  return food_json;
  })(); 
  console.log(food_json);
function dsLineChartBasics() {

	var margin = {top: 20, right: 0, bottom: 0, left: 0},
	    width = 1150 - margin.left - margin.right,
	    height = 125 - margin.top - margin.bottom
		
	    ;
		
		return {
			margin : margin, 
			width : width, 
			height : height
		}			
		;
}


function dsLineChart() {
	var basics = dsLineChartBasics();
	
	var margin = basics.margin,
		width = basics.width,
	   height = basics.height
		;

	
	var svg = d3.select("#monitorStatistics").append("svg")
	    .attr("width", width)
	    .attr("height", height)
	    // create group and move it so that margins are respected (space for axis and title)
	    
	var plot = svg
	    .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
	    .attr("id", "lineChartPlot")
	    ;

		/* descriptive titles as part of plot -- start */

    /*
	 *Total recalls since 2004
	 */
	plot.append("text")
		.text(food_json)
		.attr("id","Title2")
		.attr("x",100)
		.attr("y",45)	
		;    
	svg.append("text")
		.text("Total Queries since June 2015")
		.attr("id","Title1")	
		.attr("x",100)
		.attr("y", height-15)
		;
   
    /*
	 *Total US recalls since 2004
	 */
	plot.append("text")
		.text(518)
		.attr("id","Title2")
		.attr("x",330)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Query time (ms)")
		.attr("id","Title1")	
		.attr("x",330)
		.attr("y", height-15)
		;
		
		
	/*
	 * Total Recalls Year-to-Date
	 */
	plot.append("text")
		.text(24)
		.attr("id","Title2")
		.attr("x",530)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Total Year-to-Date Queries")
		.attr("id","Title1")	
		.attr("x",530)
		.attr("y", height-15)
		;
	
    /*
     * Average Recalls a day per calendar year
     */	 
	plot.append("text")
		.text(3.7)
		.attr("id","Title2")
		.attr("x",750)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Avg Queries per day")
		.attr("id","Title1")	
		.attr("x",750)
		.attr("y", height-15)
		;
    
	
	/*
	 * Percent Recall Change per Calendar Year
	 */
	plot.append("text")
		.text("+23%")
		.attr("id","Title2")
		.attr("x",1000)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Yearly Change in Queries")
		.attr("id","Title1")	
		.attr("x",1000)
		.attr("y", height-15)
		;
		
		

}
dsLineChart();

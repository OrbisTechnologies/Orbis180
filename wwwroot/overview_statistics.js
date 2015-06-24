/*
 *A D3 widget that displays overall statistical data for total recalls, total US *recalls,year to date recalls for the current year as well as the percent change from 
 *the previous year
 * @author Apeenan Arias
 */

/*
 * Returns the total number of results from 2004 to current
 *
 */
var food_json = (function(){
			  var food_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?api_key=LEsaCnFqu5NnXynoyvcNjtaSTEde5r7inxEfcTIH',
			  }
				).done(function(data) {
				food_json = data; 
  });
  return food_json;
  })();
  
  /*
   *Returns the total number of recalls from the US overall
   */
  var us_recalls = (function(){
			  var us_recalls = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?api_key=LEsaCnFqu5NnXynoyvcNjtaSTEde5r7inxEfcTIH&search=country:"US"',
			  }
				).done(function(data) {
				us_recalls = data; 
  });
  return us_recalls;
  })();
  
  /*
   * Returns the total number of recalls year to date
   */
  var year_to_date = (function(){
			  var year_to_date = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?api_key=LEsaCnFqu5NnXynoyvcNjtaSTEde5r7inxEfcTIH&search=recall_initiation_date:[20150101+TO+20150622]',
			  }
				).done(function(data) {
				year_to_date = data; 
  });
  return year_to_date;
  })();
  
  /*
   * Returns the total number of recalls from the previous year (2014)
   */
  var previous_date = (function(){
			  var previous_date = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?api_key=LEsaCnFqu5NnXynoyvcNjtaSTEde5r7inxEfcTIH&search=recall_initiation_date:[20140101+TO+20140622]',
			  }
				).done(function(data) {
				previous_date = data; 
  });
  return previous_date;
  })();

//Variable for the overall total recalls
var total_recalls = food_json.meta.results.total;

//Total recalls for the US alone
var us_total =  us_recalls.meta.results.total; 

//Total recalls year to date for the current year
var ytd_recalls = year_to_date.meta.results.total;

//Calculation to resolve average year to date recalls per day
var avg_per_day = d3.round((year_to_date.meta.results.total / 365),1);

//Calculation to resolve the yearly change in recalls from current year over previous year
var yearly_change = d3.round((year_to_date.meta.results.total / previous_date.meta.results.total),1);

/*
 *Method that builds the Widget with the data received from the endpoint 
 *
 */
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

	
	var svg = d3.select("#overviewStatistics").append("svg")
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
		.text(total_recalls)
		.attr("id","Title2")
		.attr("x",100)
		.attr("y",45)	
		;    
	svg.append("text")
		.text("Total Recalls since 1/1/2004")
		.attr("id","Title1")	
		.attr("x",100)
		.attr("y", height-15)
		;
   
    /*
	 *Total US recalls since 2004
	 */
	plot.append("text")
		.text(us_total)
		.attr("id","Title2")
		.attr("x",330)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Total US Recalls since 1/1/2004")
		.attr("id","Title1")	
		.attr("x",330)
		.attr("y", height-15)
		;
		
		
	/*
	 * Total Recalls Year-to-Date
	 */
	plot.append("text")
		.text(ytd_recalls)
		.attr("id","Title2")
		.attr("x",530)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Total Year-to-Date Recalls")
		.attr("id","Title1")	
		.attr("x",530)
		.attr("y", height-15)
		;
	
    /*
     * Average Recalls a day per calendar year
     */	 
	plot.append("text")
		.text(avg_per_day)
		.attr("id","Title2")
		.attr("x",750)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("Avg Recalls/day per Calendar Year")
		.attr("id","Title1")	
		.attr("x",750)
		.attr("y", height-15)
		;
    
	
	/*
	 * Percent Recall Change per Calendar Year
	 */
	plot.append("text")
		.text("+"+ yearly_change + "%")
		.attr("id","Title2")
		.attr("x",1000)
		.attr("y",45)	
		;	    
	svg.append("text")
		.text("% Recall Change per Calendar Year")
		.attr("id","Title1")	
		.attr("x",1000)
		.attr("y", height-15)
		;
		
		

}
dsLineChart();

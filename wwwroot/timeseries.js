//https://api.fda.gov/food/enforcement.json?search=recall_initiation_date:[20150101+TO+20151231]&count=recall_initiation_date
//

/*
 *Make an ajax request to return the recall dates across the year from January 2015 to December 2015
 */
var food_json = (function(){
			  var food_json = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?api_key=LEsaCnFqu5NnXynoyvcNjtaSTEde5r7inxEfcTIH&search=recall_initiation_date:[20150101+TO+20150622]&count=recall_initiation_date' }
				).done(function(data) {
				food_json = data; 
  });
  return food_json;
  })(); 
  
/*
 *Function getDates
 *@param json
 *Function that cycles through the json of recall_dates and formulates an
 *array comprised of the dates from January 2015 up to December 2015
 */
function getDates(json)
{
	var dates = [];
	dates.push('x');
	for(var i = 1; i < 85; i++)
		dates.push(json.results[i].time);
	return dates;
}
/*
 *Function getOccurrence
 *@param json
 *Function that cycles through the json of recall dates and formulates an
 *array comprised of the number of recalls for that specified date
 */
function getOccurrence(json)
{
	var data = [];
	data.push('Voluntary Recalls');
	for(var i=1; i<85;i++)
		data.push(json.results[i].count);
	return data;
}
//Variable that houses the recall dates to form the x-axis of the timeline graph
var recall_dates = getDates(food_json).slice();

//Variable that houses the count by recall date that will form the y-axis of the timeline graph
var recall_occurrence = getOccurrence(food_json).slice();
var chart = c3.generate({
    bindto: '#timeseries',
	size: {
  width: 1300,
},
    data: {
        x: 'x',
        xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
        columns: [
			recall_dates,
			recall_occurrence
        ]
    },
	subchart: {
        show: true
    },
    axis: {
        x: {
            type: 'timeseries',
            tick: {
                format: '%Y-%m-%d'
            }
        }
    }
});

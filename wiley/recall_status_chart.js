/*
 *A C3 widget that displays overall statistical data for total recalls in the form of 
 *a donut pie chart. Data that is displayed is the percentage of recalls based on the
 *categories of: Ongoing, Completed, Terminated.
 * @author Apeenan Arias
 */

var recall_status = (function(){
			  var recall_status = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?count=status.exact',
			  }
				).done(function(data) {
				recall_status = data; 
  });
  return recall_status;
  })(); 
  
var chart = c3.generate({
	bindto: '#recall_status',
	size: {
  width: 300,
  height: 300
},
    data: {
        columns: [
            [recall_status.results[0].term, recall_status.results[0].count],
			[recall_status.results[1].term, recall_status.results[1].count],
			[recall_status.results[2].term, recall_status.results[2].count]
            
        ],
        type : 'donut',
        onclick: function (d, i) { console.log("onclick", d, i); },
        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
    },
    donut: {
        title: "Recall Status"
    }
});

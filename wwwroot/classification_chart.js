/*
 *A C3 widget that displays overall statistical data for total recalls in the form of 
 *a donut pie chart. Data that is displayed is the percentage of recalls based on the
 *classification of the recall: Class I, Class II, Class III.
 * @author Apeenan Arias
 */

var class_type = (function(){
			  var class_type = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?count=classification.exact',
			  }
				).done(function(data) {
				class_type = data; 
  });
  return class_type;
  })();  
 
var chart = c3.generate({
	bindto: '#classification',
	size: {
  width: 300,
  height: 300
},
    data: {
        columns: [
		    [class_type.results[1].term, class_type.results[1].count],
            [class_type.results[0].term, class_type.results[0].count],
			[class_type.results[2].term, class_type.results[2].count]
        ],
        type : 'donut',
        onclick: function (d, i) { console.log("onclick", d, i); },
        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
    },
    donut: {
        title: "Classification Type"
    }
});
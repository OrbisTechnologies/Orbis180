/*
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
*/
var chart = c3.generate({
	bindto: '#monitorseries',
	size: {
  width: 1000,
},
    data: {
        x: 'x',
//        xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
        columns: [
            ['x', '2015-06-29'],
//            ['x', '20130101', '20130102', '20130103', '20130104', '20130105', '20130106'],
            ['Queries', 114],
        ]
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

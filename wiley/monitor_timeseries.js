
var class_type = (function(){
			  var class_type = null;
			  $.ajax({
			  'async': false,
	          url: 'http://wiley.orbistechnologies.com/dataIngestion/rest/monitor/querytimes',
			  }
				).done(function(data) {
				class_type = data; 
  });
  return class_type;
  })();  
  var queryDates = [];
  var Queries = [];
  queryDates.push('x');
  Queries.push("Queries");
  for(var i = 1; i <= class_type.length; i++){
	queryDates.push(class_type[i-1].Date);
	Queries.push(class_type[i-1].Count);
  }
  queryDates = queryDates.slice();
  Queries = Queries.slice();
  
var chart = c3.generate({
	bindto: '#monitorseries',
	size: {
  width: 1000,
},
    data: {
        x: 'x',
        xFormat: '%Y-%m-%d', // 'xFormat' can be used as custom format of 'x'
        columns: [
            queryDates,
//            ['x', '20130101', '20130102', '20130103', '20130104', '20130105', '20130106'],
            Queries
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

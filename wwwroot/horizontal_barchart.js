/*
 *A C3 widget that displays overall statistical data for total recalls in the form of 
 *a horizontal bar chart. Data that is displayed is the percentage of recalls based on
 *categories of notification types: Email, Letter, Telephone, Visit, Press Release
 * @author Apeenan Arias
 */

var notification_type = (function(){
			  var notification_type = null;
			  $.ajax({
			  'async': false,
	          url: 'https://api.fda.gov/food/enforcement.json?count=initial_firm_notification.exact',
			  }
				).done(function(data) {
				notification_type = data; 
  });
  return notification_type;
  })();
  var email = 0,letter = 0,phone = 0,visit = 0,press = 0,more = 0,other = 0, combo = 0;
  function getData(json)
  {
	  for(var i = 0; i < 17; i++)
	  {
		  if(json.results[i].term == "Email" || json.results[i].term == "E-Mail" || json.results[i].term == "email")
		  {
			  email += json.results[i].count;
		  }
		  else if(json.results[i].term == "Letter" || json.results[i].term == "letter")
		  {
			  letter += json.results[i].count;
		  }
		  else if(json.results[i].term == "Telephone" || json.results[i].term == "telephone" || json.results[i].term == "Phone")
		  {
			  phone += json.results[i].count;
		  }
		  else if(json.results[i].term == "Visit")
		  {
			  visit = json.results[i].count;
		  }
		  else if(json.results[i].term == "Press Release" || json.results[i].term == "press release" || json.results[i].term == "Press")
		  {
			  press += json.results[i].count;
		  }
		  else if(json.results[i].term == "Other")
		  {
			  other = json.results[i].count;
		  }
		  else if(json.results[i].term == "Combination")
		  {
			  combo = json.results[i].count;
		  }
		  else if(json.results[i].term == "Two or more of the following: Email, Fax, Letter, Press Release, Telephone, Visit")
		  {
			  more = json.results[i].count;
		  }
	  }
	 
  };
  getData(notification_type);
c3.generate({
  bindto: '#horizontal_barchart',
  size: {
  width: 500,
  height: 300
},
  data: {
    columns: [
      ['E-mail', email],
	  ['Press Release',press],
	  ['Telephone',phone],
	  ['Visit',visit],
	  ['Letter',letter],
	  ['Two or More', more],
	  ['Combination',combo]
    ],
    type: 'bar',
  },
  bar: {
        width: {
            ratio: 1.0 // this makes bar width 50% of length between ticks
        }
  },
  axis: {
    rotated: true,
    x: {
      show: false
    }
  }
});

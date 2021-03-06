function getNotifications(status, limit) {
	$.ajax({
      	dataType: "json",
      	url: "webresources/api/getNotifications/"+ status+"/"+limit,
      	success: function(json){
		  var chartdownloads = document.getElementById('listnotifications');
		  chartdownloads.innerHTML = '';
		  var notarr = json.Notifications;
		  notarr.forEach(function(json){
			  var text = '<a href="#" class="list-group-item">';
			  text = text + '<span class="badge">' + json.DATE + '</span>';
			  text = text + '<i class="fa fa-fw '
			  if(json.STATUS == 2) {
			  		text = text + 'fa-bell-o ';
			  } else if(json.STATUS == 3) {
			  		text = text + 'fa-exclamation-circle';
			  } else if(json.STATUS == 4) {
				  text = text + 'fa-magnet';
			  } else if(json.STATUS == 5) {
				  text = text + 'fa-download';
			  } else if(json.STATUS == 6) {
				  text = text + 'fa-download';
			  }
			  text = text + '"></i> ' + json.MESSAGE + '</span>';
			  text = text + '</a>';
			  chartdownloads.innerHTML = chartdownloads.innerHTML + text;
		  });
  		}
    });
}

function clearlog(){
    $.ajax({
      	dataType: "json",
      	url: "webresources/api/removeNotifications",
      	success: function(){
            var chartdownloads = document.getElementById('listnotifications');
            chartdownloads.innerHTML = '';
        },
        error: function(){
            alert('error');
        }
    });
}

function docloaded() {
    getNotifications(0, 0);
}

var selectedepisodes = [];
var episodeCountPerSeason = [];
var showid = 0;

function getShowDetails() {
        if (showid == 0){
            showid = getParameterByName('id');
        }
        selectedepisodes = [];
	$.ajax({
		dataType: "json",
		url: "webresources/api/getFullShowObject/" + showid,
		success: function(json){
			var showtitle = document.getElementById('showtitle');
                        var qua = '';
                        if(json.quality != null){
                            qua = '<small> ' + json.quality +'</small>';
                        } else{
                            qua = '<small> HDTV</small>';
                        } 
			showtitle.innerHTML = json.tvdbinfo.Showname + qua;
			var showinfo = document.getElementById('showinfo');
			showinfo.innerHTML = json.tvdbinfo.Overview;
			var showseason = document.getElementById('showseasons');
			showseason.innerHTML = '';
			var seasoncount = json.tvdbinfo.SeasonCount;
			var text = '';
			for (seasonnr = seasoncount; seasonnr >= 1; seasonnr--) {
				var jsonseaon = json.tvdbinfo.SeasonList["Season " + seasonnr];
				text = text + '<div class="panel panel-default">';
				text = text + '<div class="panel-heading">';
				text = text + '<input type="checkbox" name="" value="'+ seasonnr +'" onchange="cboChangedSeason('+seasonnr+', this)"> Season ' + seasonnr;
				text = text + '</div>';
				text = text + '<div class="panel-body">';
				text = text + '<div class="table-responsive">';
				text = text + '<table class="table">';
				text = text + '<thead>';
				text = text + '<tr>';
				text = text + '<th>Mark</th>';
				text = text + '<th>#</th>';
				text = text + '<th>Episode Title</th>';
				text = text + '<th>Airdate Title</th>';
				text = text + '<th>Status</th>';
				text = text + '<th>Search</th>';
				text = text + '</tr>';
				text = text + '</thead>';
				text = text + '<tbody>';
				var countepisode = 0;
				jsonseaon.forEach(function(jsonepisode){
					var cbovalue = 'S';
					if(jsonepisode.Season <= 9)
						cbovalue = cbovalue + '0'+ jsonepisode.Season;
					else
						cbovalue = cbovalue + jsonepisode.Season;

					cbovalue = cbovalue + 'E';
					if(jsonepisode.EpisodeNumber <= 9)
						cbovalue = cbovalue + '0'+ jsonepisode.EpisodeNumber;
					else
						cbovalue = cbovalue + jsonepisode.EpisodeNumber;
                                        var statusvalue = json.tvdbinfo.EpisodeStatus[cbovalue];
                                        var statustext = '';
                                        if(statusvalue == 0){
						text = text + '<tr id="tr'+cbovalue+'" class="info">';
                                                statustext = 'ignored';
					}else if(statusvalue == 1){
						text = text + '<tr id="tr'+cbovalue+'" class="warning">';
                                                statustext = 'wanted';
					}else if(statusvalue == 2){
						text = text + '<tr id="tr'+cbovalue+'" class="success">';
                                                statustext = 'downloaded';
					}else if(statusvalue == 3){
						text = text + '<tr id="tr'+cbovalue+'" class="warning">';
                                                statustext = 'error';
					}
					
					text = text + '<td style="width:10px" align="center"><input type="checkbox" id="S'+seasonnr+'E'+ jsonepisode.EpisodeNumber + '" name="" value="'+cbovalue+'" onchange="cboChanged(this)"></td>';
					text = text + '<td>' + jsonepisode.EpisodeNumber + '</td>';
					if(jsonepisode.EpisodeName == ''){
						text = text + '<td>' + 'TBA' + '</td>';
					} else {
						text = text + '<td>' + jsonepisode.EpisodeName + '</td>';
					}
					text = text + '<td>' + jsonepisode.AirDate + '</td>';
					text = text + '<td>' + statustext + '</td>';
                                        //<button class="btn btn-success" onclick="markEpisodeStatus(1)"><i class=" fa fa-star "></i> Mark Wanted</button>
					text = text + '<td style="width:10px" align="center"><button style="border:0; background: none;" onclick="manualsearch(\''+cbovalue+'\',\''+json.quality+'\')"><i class="fa fa-fw fa-search search"></i></button></td>';
					text = text + '</tr>';
					countepisode++;
				});
				episodeCountPerSeason[seasonnr] = countepisode;
				text = text + '</tbody>';
				text = text + '</table>';
				text = text + '</div>';
				text = text + '</div>';
				text = text + '</div>';
			}
			showseason.innerHTML = text;
			text = '';
		}
	});
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function cboChanged(cbo){
    if(cbo.checked){
            selectedepisodes[selectedepisodes.length] = cbo.value;
    } else {
            var index = selectedepisodes.indexOf(cbo.value);
            selectedepisodes.splice(index, 1);
    }
}

function cboChangedSeason(seasonnr, cbo){
    var count  = episodeCountPerSeason[seasonnr];
    for (episodenr = 1; episodenr <= count; episodenr++) {
        var cboepisode = document.getElementById('S'+seasonnr+'E'+episodenr);
        if(cbo.checked){
                cboepisode.checked = true;
                selectedepisodes[selectedepisodes.length] = cboepisode.value;
        } else {
                cboepisode.checked = false;
                var index = selectedepisodes.indexOf(cboepisode.value);
                selectedepisodes.splice(index, 1);
        }
    }
}

function markEpisodeStatus(status){
    console.log('ok');
    var jsonpost = '{';
    selectedepisodes.forEach(function(episode){
        if(jsonpost.length == 1){
            jsonpost = jsonpost + '"' + episode + '":' + status;
        } else {
             jsonpost = jsonpost + ', "' + episode + '":' + status;
        }
    });
    jsonpost = jsonpost + '}';
    console.log(jsonpost);
    $.ajax({
        type: "POST",
        url: "webresources/api/updateEpisodeStatus/" + showid,
        data: jsonpost,
        contentType:"application/json",
        //dataType: "application/json",
        success: function(a){
            //var showseason = document.getElementById('showseasons');
            //showseason.innerHTML = '';
            getShowDetails();
        },
        error: function(a){
        }
    });  
}

function removeShow(){
    var x;
    if (confirm("Remove show?") == true) {
        x = true;
    } else {
        x = false;
    }
    if(x == true){
        $.ajax({
            dataType: "json",
            url: "webresources/api/removeShow/" + showid,
            success: function(json){
                window.location.href = 'ShowList.html';
            },
            error: function(a){
            }
        });  
        
    }
}

function manualsearch(episode, quality){
    if(episode != null && quality != null){
        $.ajax({
            dataType: "json",
            url: "webresources/api/search/" + showid +"/"+episode+"/"+quality,
            success: function(json){
            },
            error: function(a){
            }
        });
    }
}

function editshow(){
    if(showid != 0){
        window.location = "AddShow.html?id=" + showid;
    }
}

function linkFiles(){
	if(showid != 0){
        window.location = "LinkFiles.html?id=" + showid;
    }
}

function docloaded() {
    getShowDetails();
}
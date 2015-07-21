function getShortShowList() {
    $.ajax({
        dataType: "json",
        url: "webresources/api/getTLDRshows",
        success: function(json){
            var listbody = document.getElementById('showlistbody');
            listbody.innerHTML = '';
            var Shows = json.Shows;
            var counter = 1;
            Shows.forEach(function(jsonshow){
                var episodetotal = jsonshow.EpisodeCount;
                var episodesignored = jsonshow.EpisodesIgnored;
                var episodeswanted = jsonshow.EpisodesWanted;
                var episodedownloaded = jsonshow.EpisodesDownloaded;
                var percentage = 100;
                episodetotal = episodetotal - episodesignored;
                if(episodetotal != 0){
                        percentage = (episodedownloaded/episodetotal)*100;
                }
                var text = '<tr>';
                text = text + '<td>' + counter.toString() + '</td>';
                text = text + '<td><a href="ShowDetail.html?id=' + jsonshow.ID + '">' + jsonshow.Showname + '</a></td>';
                text = text + '<td>' + jsonshow.NextAirdate + '</td>';
                text = text + '<td>' + jsonshow.Status + '</td>';
                text = text + '<td><div class="progress"><div class="progress-bar progress-bar-success" role="progressbar" style="width: '+percentage+'%"></div></div></td>';
                text = text + '<td style="width:10px" align="center">' + episodedownloaded + '/' + episodetotal +'</td>';
                text = text + '</tr>';
                listbody.innerHTML = listbody.innerHTML + text;
                counter++;
            });
        }
    });
}

function getRequests(){
    var showRequestList = document.getElementById('showRequestList');
    showRequestList.innerHTML = '';
    $.ajax({
        dataType: "json",
        url: "webresources/api/getRequestDownloads",
        async: true,
        success: function(json){
            json.forEach(function(jsonrequest){
                showRequestList.innerHTML = showRequestList.innerHTML + '<tr><td><button class="btn btn-danger" onclick="removerequest('+jsonrequest.ID+')"><i class=" fa fa-trash-o "></i> </button></td><td>'+jsonrequest.FILENAME +'</td></tr>';
            });
        }
    });
}

function removerequest(id){
    console.log(id);
    //removeRequestDownload
    $.ajax({
        dataType: "json",
        url: "webresources/api/removeRequestDownload/"+id,
        async: true,
        success: function(json){
            getRequests();
        }
    });
}

function process(){
    var n = noty({
        text: '<img src=\"images/ajax-loader.gif\"/> Processing...',
        layout: 'topCenter',
        theme: 'relax',
        killer: true,
        type: 'information'
    });
    $.ajax({
        dataType: "json",
        url: "webresources/api/process",
        async: true,
        success: function(json){
            var n = noty({
                text: 'Processed all shows and requests',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'success'
            });
            getRequests();
        },
        error: function(){
            var n = noty({
                text: 'Could not process shows',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'error'
            });
        }
    });
}

function startProcessTimer(){
    $.ajax({
        dataType: "json",
        url: "webresources/api/processTimer",
        async: true,
        success: function(json){
            var n = noty({
                text: 'Timer started.',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'success'
            });
            getRequests();
        },
        error: function(){
            var n = noty({
                text: 'Could not start timer.',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'error'
            });
        }
    });
}

function lastProcess(){
    $.ajax({
        dataType: "json",
        url: "webresources/api/lastProcessCheck",
        async: true,
        success: function(json){
            if(json.lastProcessCheck){
                document.getElementById("lastProcessCheck").innerHTML = "Last check: " + json.lastProcessCheck;
            } else {
                document.getElementById("lastProcessCheck").innerHTML = "";
            }
           
        },
        error: function(){
            var n = noty({
                text: 'Could not start timer.',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'error'
            });
        }
    });
    
}

function stopProcessTimer(){
    $.ajax({
        dataType: "json",
        url: "webresources/api/StopProcessTimer",
        async: true,
        success: function(json){
            var n = noty({
                text: 'Timer stopped.',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'success'
            });
            getRequests();
        },
        error: function(){
            var n = noty({
                text: 'Could not stop timer.',
                layout: 'topCenter',
                theme: 'relax',
                killer: true,
                type: 'error'
            });
        }
    });
}

function docloaded() {
    getShortShowList();
    getRequests();
    lastProcess();
}
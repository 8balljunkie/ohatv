var showid = 0;
var alldata;
var seasoncount = 1;

function getAllFiles(){
	$.ajax({ 
		type: "GET",   
		url: 'webresources/api/ManualLinkFiles/' + showid,  
		async: false,       
		success : function(data){
			alldata = data;
			var hasmoreseasons = true;
			while(hasmoreseasons){
				for (var key in alldata.SearchAndFileLink) {
                                        var search = 'S0';
                                        if(seasoncount > 9){
                                            search = 'S';
                                        }
					if(key.indexOf(search+seasoncount) > -1) {
						seasoncount++;
						hasmoreseasons = true;
						break;
					} else {
						hasmoreseasons = false;
					}
				}
			}
                        seasoncount = seasoncount - 1;
			var element = document.getElementById('Seasons');
                        element.innerHTML = 'Seasons: ';
			for(var i = 1; i<=seasoncount; i++){
			element.innerHTML = element.innerHTML + '<a onclick="showSeason('+i+')"> S0'+i+'</a>'; 
			}
                        showSeason(seasoncount);
                        
                        document.getElementById('listfiles').innerHTML = '';
                        for(var i = 0; i < alldata.FILES.length; i++){
                            var htmlvalue = '<tr>';
                                htmlvalue = htmlvalue +'<td><input type="radio" name="group1" value= '+ i +'  /></td>';
                                htmlvalue = htmlvalue +'<td> ' + alldata.FILES[i].FILENAME +'</td>';
                                htmlvalue = htmlvalue +'</tr>';
                            var elementhtml = document.getElementById('listfiles').innerHTML;
                            document.getElementById('listfiles').innerHTML = elementhtml + htmlvalue;
                        }
                        
		},
		error : function(){
			alert('Can\'t find show');
			return;
		}
	});
}

function showSeason(seasonnumber){
    document.getElementById('Episodelist').innerHTML = '';
    var search = 'S0';
    if(seasonnumber > 9){
        search = 'S';
    }
    for (var key in alldata.SearchAndFileLink) {
        if(key.indexOf(search+seasonnumber) > -1) {
            var htmlvalue = '<tr>';
                htmlvalue = htmlvalue +'<td><input type="radio" name="group2" value="'+ key +'" /></td>';
                htmlvalue = htmlvalue +'<td> ' + alldata.SearchAndFileLink[key] +'</td>';
                htmlvalue = htmlvalue +'</tr>';
            var elementhtml = document.getElementById('Episodelist').innerHTML;
            document.getElementById('Episodelist').innerHTML = elementhtml + htmlvalue;
        }
     }
}

function linkfile(){
    var postitems = [];
    var radioFiles = document.getElementsByName("group1");
    var radioEpisodes = document.getElementsByName("group2");
    var checkedFileid = -1;
    var checkedEpisodeid = -1;
    for(var i = 0; i < radioFiles.length; i++){
        if(radioFiles[i].checked == true){
            checkedFileid = i;
            break;
        }
    }
    
    for(var i = 0; i < radioEpisodes.length; i++){
        if(radioEpisodes[i].checked == true){
            checkedEpisodeid = i;
            break;
        }
    }
  
    if(checkedFileid != -1 && checkedEpisodeid != -1){
        var obj = new Object();
        obj.SHOWID = showid;
        obj.SeasonEpisodeNumber = radioEpisodes[checkedEpisodeid].value;
        obj.ShowfileName = alldata.SearchAndFileLink[obj.SeasonEpisodeNumber];
        obj.filepath = alldata.FILES[radioFiles[checkedFileid].value].FILEPATH;
        console.log(obj);
        
        $.ajax({ 
            type: "POST",   
            url: 'webresources/api/LinkFileWithEpisode',  
            async: false,
            contentType:"application/json",
            data : JSON.stringify(obj),
            success : function(data){
                getAllFiles();
            },
            error : function(){
                alert('Oops error');
                return;
            }
        });  
    }
    
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function docloaded() {
	showid = getParameterByName('id');
	if(showid != 0){
		getAllFiles();
	}
}
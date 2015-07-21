var showid = 0;
var datasearched;

function addshow(){
    if(showid == 0){
        //var tvdbid = document.getElementById('tvdbid').value;
        var radiobuttons = document.getElementsByName('shows');
        console.log(radiobuttons);
        var tvdbid;
        for(var i=0; i < radiobuttons.length; i++){
            var element = radiobuttons[i];
            if(element.checked){
                tvdbid = element.value;
            }
        }
        var showrssid = document.getElementById('showrssid').value;
        if(!(showrssid)){
            showrssid = 0;
        } 
        var quality = document.getElementById('quality').value;
        if(tvdbid && quality){
            $.ajax({
                dataType: "json",
                url: "webresources/api/addShow/"+tvdbid+"/"+showrssid+"/"+quality,
                success: function(json){
                    window.location = "ShowList.html";
                },
                error: function(){
                }
            });
        }
    } else {
        updateshow();
    }
}

function updateshow(){
    var showrssid = document.getElementById('showrssid').value;
    //var eztv = document.getElementById('eztv').value;
    var quality = document.getElementById('quality').value;
    
     $.ajax({
        dataType: "json",
        url: "webresources/api/updateShow/"+showid+"/"+showrssid+"/"+quality,
        success: function(json){
            window.location = "ShowList.html";
        },
        error: function(){
        }
    });
}

function searchshow(name){
    document.getElementById('listshows').innerHTML = '';
    $.ajax({
        dataType: "json",
        url: "webresources/api/searchshow/" + name,
        async: true,
        success: function(data){
            document.getElementById("rowshowlists").style.display = '';
            for(var i=0; i < data.length; i++){
                datasearched = data;
                var json = data[i];
                var html = '<tr>';
                    html = html + '<td><input type="radio" name="shows" value="'+json.thetvdbid+'" /></td>';
                    html = html + '<td>'+json.Showname+'</td>';
                    html = html + '<td>'+json.firstaired+'</td>';
                    html = html + '<td>'+json.Network+'</td>';
                    html = html + '<td><a href="http://www.thetvdb.com/?tab=series&lid=7&id='+json.thetvdbid+'">'+json.thetvdbid+'</a></td>';
                    html = html + '<td><a href="http://www.imdb.com/title/'+json.IMDBID+'">'+json.IMDBID+'</a></td>';
                    html = html + '</tr>';
                document.getElementById('listshows').innerHTML = document.getElementById('listshows').innerHTML + html;
            }
        },
        error: function(){
        }
    });
    
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}



function docloaded(){
    if(getParameterByName('id')){
        showid = getParameterByName('id');
        $.ajax({
            dataType: "json",
            url: "webresources/api/getFullShowObject/" + showid,
            success: function(json){
                document.getElementById("tvdbid").setAttribute("disabled", "");
                document.getElementById("tvdbid").value = json.thetvdbid;
                document.getElementById('showrssid').value  = json.showrssid;
                document.getElementById('eztv').value  = json.eztv;
                if(json.quality!= null){
                    document.getElementById('quality').value = json.quality;
                } else {
                    document.getElementById('quality').value = 'HDTV';
                }
                
            },
            error: function(){
            }
        });
    }
}



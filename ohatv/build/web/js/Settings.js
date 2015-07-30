function savebtclient(){
	var jsonobj = new Object();
	jsonobj.bturl = document.getElementById('url').value;
	jsonobj.btclient = document.getElementById('btclientselect').value;
	jsonobj.username = document.getElementById('username').value;
	jsonobj.password = document.getElementById('password').value;
	jsonobj.btpath = document.getElementById('path').value;
	jsonobj.MoveTo = document.getElementById('proToFolder').value;
	jsonobj.btignorewords = document.getElementById('ignoredwords').value;
	var dataobj = JSON.stringify(jsonobj);
	
	$.ajax({
		type: "POST",
		url: "webresources/api/updatebtclient",
		data: dataobj,
		contentType:"application/json",
		success: function(){
                    getSettings();
		},
		error: function(){
		}
	});
}

function getSettings(){
    $.ajax({
            type: "GET",
            url: "webresources/api/getsettings",
            contentType:"application/json",
            success: function(data){
                console.log(data);
                if(data.btclient){
                    document.getElementById('btclientselect').value = data.btclient;
                }
                if(data.bturl){
                    document.getElementById('url').value = data.bturl;
                }
                if(data.btpath){
                    document.getElementById('path').value = data.btpath;
                }
                if(data.btpath){
                    document.getElementById('path').value = data.btpath;
                }
                if(data.MoveTo){
                    document.getElementById('proToFolder').value = data.btpath;
                }
                if(data.btignorewords){
                    document.getElementById('ignoredwords').value = data.btignorewords;
                }
            },
            error: function(){
            }
	});
}

function haschanged(element){
    if(element.value == 'Blackhole'){
        document.getElementById("url").disabled = true;
    } else {
        document.getElementById("url").disabled = false;
    }
}

function docloaded(){
    getSettings();
}
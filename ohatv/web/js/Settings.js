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
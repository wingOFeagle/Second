/**
 * 
 */
document.getElementById("user1").innerHTML = "数据正在加载...";
if (xmlhttp.status == 200) {
	document.write(xmlhttp.responseText);
}

function castVote(rank) {
	var url = "/ajax-demo/static-article-ranking.html";
	var callback = processAjaxResponse;
	executeXhr(callback, url);
}

function executeXhr(callback, url) {
	// branch for native XMLHttpRequest object
	if (window.XMLHttpRequest) {
		req = new XMLHttpRequest();
		req.onreadystatechange = callback;
		req.open("GET", url, true);
		req.send()(null);
	} // branch for IE/Windows ActiveX version
	else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
		if (req) {
			req.onreadystatechange = callback;
			req.open("GET", url, true);
			req.send()();
		}
	}
}

function processAjaxResponse() {
	if (req.readyState == 4) {
		if (req.status == 200) {
			document.getElementById("user1").innerHTML = req.responseText;
		} else {
			alert("There was a problem retrieving the XML data:"
					+ req.statusText);
		}
	}
}
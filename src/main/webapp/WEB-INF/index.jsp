<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title>Welcome</title>

<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.9.1/bootstrap-table.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">


<style>
#progress { position:relative; width:10px; border: 1px solid #ddd; padding: 1px; border-radius: 3px; }

</style>
<body>
<div class="container">


<table id="table"
 		   data-toggle="table"
           data-toolbar="#toolbar"
           data-search="true"
           data-show-refresh="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-export="true"
           data-minimum-count-columns="2"
           data-pagination="true"
           data-page-list="[10, 25, 50, 100, ALL]"
           data-side-pagination="client"
           data-id-field="processId"
           data-url="./getwork"
           data-height="299"
           data-mobile-responsive="true"
           data-cache="false"
           class="table table-striped">
    <thead>
        <tr>
            <th data-field="processId" data-sortable="true">Process id</th>
            <th data-field="dateTime" data-sortable="true">Creation date</th>
            <th data-field="operate" data-formatter="operateFormatter" data-events="operateEvents" data-sortable="false">Operation</th>
        </tr>
    </thead>
    
</table>

<button type="button" id="addWorkButton" class="btn btn-primary" >
  Add work
</button>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

</div>
<!-- Bootstrap core JavaScript
   ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script type="text/javascript" src="//cdn.jsdelivr.net/sockjs/1.0.0/sockjs.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.9.1/bootstrap-table.min.js"></script>

<script src="${contextPath}/js/stomp.min.js"></script>

<script>

// Open the web socket connection.
var sock = new SockJS('${contextPath}/progress');
var stompClient = Stomp.over(sock);

//Callback function to be called when stomp client is connected to server
var connectCallback = function() {
  stompClient.subscribe('/topic/progress', renderProgress);
}; 
 
// Callback function to be called when stomp client could not connect to server
var errorCallback = function(error) {
  alert(error.headers.message);
};
 
// Connect to server via websocket
stompClient.connect("guest", "guest", connectCallback, errorCallback);

//Render data from server into HTML, registered as callback
//when subscribing to topic
function renderProgress(frame) {
	var data = JSON.parse(frame.body);
	var percent = data.precentComplete;
	var message = data.message;
	var id = data.id;
	$('#progress-bar' + id).css('width', percent+'%').attr('aria-valuenow', percent);
	$('#progress-bar' + id).text(percent+'%' + ' ' + message);
}

var $table = $('#table');

//formats the operator field
function operateFormatter(value, row, index) {
	if (row.state===2) { //process finished so stop showing progress bar.
	    return [
	        '<a class="remove" href="javascript:void(0)" title="Remove">',
	        '<i class="glyphicon glyphicon-remove"></i>',
	        '</a>'
	    ].join('');
	}
	else if (row.state===1) { //process state started, return current progress bar
		return '<div id="progress' +row.processId+ '" class="progress">' + $('#progress' + row.processId).html() + '</div>';
	}
	else  { //process not started yet, so show new progress bar
		return [
'<div id="progress' +row.processId+ '" class="progress">',
'<div class="progress-bar" id="progress-bar' +row.processId+ '" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%; min-width: 2em;" >0%',
'</div></div>',
		    ].join('');
	}
}
//defines operate events, in this case removal of process
window.operateEvents = {
    'click .remove': function (e, value, row, index) {
        $table.bootstrapTable('remove', {
            field: 'processId',
            values: [row.processId]
        });
        removeProcess([row.processId]);
    }
};
//removes process server side
function removeProcess(id) {
	$.ajax({
        type:"POST",
        url: "./removework/" + id,
        success: function(msg) {
        	$table.bootstrapTable('refresh');
        }
	});
}
$('#addWorkButton').on('click', function () {
	addProcess();
  })
//adds process server side
function addProcess() {
	$.ajax({
        type:"POST",
        url: "./addwork/",
        success: function(msg) {
        	$table.bootstrapTable('refresh');
        }
	});
}

</script>
</body>
</html>
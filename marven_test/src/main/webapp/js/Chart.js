/**
 * 
 */

function getData() {
	var datas = {
		starttime : $("#sdate").val(),
		endtime : $("#edate").val()
	};
	var html = $.ajax({
				url : '/marven_test/GetQueryTime1',
				type : 'GET',
				data : datas,
				async : false,
				dataType : 'text',
				timeout : 1000,
				cache : false,
				success : succFunction
			}).responseText;

	function succFunction(tt) {
	}
	return html;
}
function ff() {
	var ctx = document.getElementById("myChart");
	var tt = getData();
	// console.log(tt);
	var nums = new Array();
	var times = new Array();
	var json = eval("(" + tt + ")"); // 数组
	$.each(json, function(index, item) {
				var num = json[index].num;
				var time = json[index].time;
				nums[index] = num;
				times[index] = time;
			});
	// console.log("labels:" + names);
	// console.log("data_v: " + ages);
	var myChart = new Chart(ctx, {
				type : 'bar',
				data : {
					labels : times,
					datasets : [{
						label : '# of Votes',
						data : nums,
						// 下面这个背景可以随机产生
						backgroundColor : ['rgba(255, 99, 132, 0.2)',
								'rgba(54, 162, 235, 0.2)',
								'rgba(255, 206, 86, 0.2)',
								'rgba(75, 192, 192, 0.2)',
								'rgba(153, 102, 255, 0.2)',
								'rgba(255, 159, 64, 0.2)'],
						borderColor : ['rgba(255,99,132,1)',
								'rgba(54, 162, 235, 1)',
								'rgba(255, 206, 86, 1)',
								'rgba(75, 192, 192, 1)',
								'rgba(153, 102, 255, 1)',
								'rgba(255, 159, 64, 1)'],
						borderWidth : 1
					}]
				},
				options : {
					scales : {
						yAxes : [{
									ticks : {
										beginAtZero : true
									}
								}]
					}
				}
			});
}
/*$(function() {
 $("#check_id").change(function() {
 var att = $("#check_id").attr("checked");
 console.log("check_id changed:" + att);
 var IntervalId = setInterval(ff, 5000);
 if (att != "checked") {
 console.log("ClearInterval");
 clearInterval(IntervalId);
 }

 })
 })
 */
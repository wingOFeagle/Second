$(function() {

			$("#button1").bind("click", function() {
						$("#button1_p").css({
									"color" : "red"
								}).text("Hello World!");
						$(".nav_p").css({
									"color" : "blue"
								}).text("Hello World!");

					});
			$(".f-f0").bind("mouseover", function() {
				$(".nav_p").text("这是一个触碰测试: " + $(this).text() + "!").bind(
						"mouseleave", function() {
							$("#button1_p").text("hello world!");
						});
			});
			$(".allschool2").bind("mouseover", function() {
						$(".zyjs_sr_special_gdxx").css({
									"display" : "block",
									"margin-left" : "650px"
								});
					}).bind("mouseleave", function() {
						$(".zyjs_sr_special_gdxx").css({
									"display" : "none"
								});
					});
			$(".allorg2").bind("mouseover", function() {
						$(".zyjs_sr_special_zjg").css({
									"display" : "block",
									"margin-left" : "720px"
								});
					}).bind("mouseleave", function() {
						$(".zyjs_sr_special_zjg").css({
									"display" : "none"
								});
					});

		})

function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}
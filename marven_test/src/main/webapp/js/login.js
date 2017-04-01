/**
 * 
 */
// 按钮验证
function Verify() {
	var resultUser = checkUser($("#txtUser"), false);
	var resultPwd = checkPwd($("#txtPwd"));
	if (resultUser && resultPwd) {
		$("#btnLogin").val("登录中...");
		console.log("正在登录中...");
		return true;
	}
	return false;
}
// 检测输入
function checkUser(obj, async) {
	var result = false;
	var v = obj.val();
	var id = obj.attr("id");

	if (IsNullOrEmpty(v) || v == obj.attr("placeholder")) {
		alert(obj.attr("placeholder"));
		return false;
	} else {
		return true;
	}
}
function checkPwd(obj) {
	var result = false;
	var v = obj.val();
	var id = obj.attr("id");
	if (IsNullOrEmpty(v) || v == obj.attr("placeholder")) {
		alert("请输入密码");
	} else {
		result = true;
	}
	return result;
}

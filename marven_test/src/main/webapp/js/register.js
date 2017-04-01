function IsNullOrEmpty(v) {
	if (v == null || v == "undefined" || v == "")
		return true;

	return false;
}
function checkUser(obj, async) {
	// 下面是用来检测数据用户的合法性
	var result1 = false;
	var v = obj.val();
	var id = "txtUser";

	var apiUrl = "/marven_test/Register1";
	var data = {
		"method" : "CheckUser",
		"txtUser" : v
	};
	// 回调函数
	var callback = function(result) {
		if (result == 1) {
			alert(v + "该账号已存在，请直接登录");
			return false;
		} else {
			console.log("返回true");
			result1 = true;
			console.log("result1被赋值为true");
			return true;
		}
	};

	if (IsNullOrEmpty(v) || v == obj.attr("placeholder")) {
		alert(id + obj.attr("placeholder"));
		return false;
	} else {
		if (async) {
			console.log("异步开始验证");
			$.post(apiUrl, data, callback);
		} else// 同步
		{
			console.log("同步验证");
			$.ajaxSetup({
						async : false
					});
			$.post(apiUrl, data, callback);
		}
	}
	return result1;
}
// 密码必须6到15位
function IsPwdStrong(s) {
	if (s.length < 3)
		return false;
	return true;
}
function checkPwd(obj) {
	var result = false;
	var v = obj.val();
	var id = "txtUser";
	if (IsNullOrEmpty(v) || v == obj.attr("placeholder")) {
		alert(id, obj.attr("placeholder"));
	} else {

		if (IsPwdStrong(v) == false) {
			alert(id + "密码必须6-20位");
			return false;
		}
		result = true;
	}
	return result;
}

// 验证两次密码一致
function checkPwd2Same() {
	var id = "txtUser";
	if ($("#txtPwd").val() != $("#txtPwd2").val()) {
		alert(id, "两次密码不一致");
		return false;
	}
	return true;
}
// 按钮验证
function Verify() {
	// 验证接收条款

	var accept = $("input[type='checkbox']");
	if (accept != null && accept.is(":checked") == false) {
		alert("请先选中“我接受并同意《用户服务条款》”才能注册");
		return false;
	}

	var result = true;
	// 依次检测用户、密码、验证密码的合法性
	console.log("开始验证user");
	var result = checkUser($("#txtUser"), false);

	console.log("result: " + result);
	if (result) {
		console.log("开始验证密码");
		result = checkPwd($("#txtPwd"));
	}

	if (result) {
		console.log("开始验证再输入密码");
		result = checkPwd($("#txtPwd2"));
	}

	if (result) {
		console.log("开始验证密码一致性");
		result = checkPwd2Same();
	}
	if (result) {
		console.log("开始注册...");
		$("#btnRegister").val("正在注册...");
		return true;
	}
	return false;
}
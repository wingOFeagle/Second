/**
 * 
 */
$(function() {
			$.ajax({
						url : '/marven_test/UpdateQueryTime1',
						type : 'GET',
						async : false,
						dataType : 'text',
						timeout : 1000,
						cache : false,
						success : succFunction
					});

			function succFunction(tt) {
			}
		})
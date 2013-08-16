<div class="wrapper col3">
  	<div id="container">
  	<div class="innercontainer">
		<div>	
			<div id="results" style="text-align: left;"></div>
	  	</div>
	</div>
	</div>
</div>
	<script>
		var fileName = '';
	
		$(document).ready(function() {
			$('#fileupload').fileupload({
				url: "${request.getContextPath()}/upload/annotationFile",
	        	maxFileSize: 2000000,
	        	acceptFileTypes: /(\.|\/)(json)$/i,
		        dataType: 'json',
		        singleFileUploads: true,
		        done: function (e, data) {
		            data.context.text('Upload finished!');
		            var myVar = setTimeout(function(){myTimer()},1000);
		            function myTimer() {
		            	//data.context.text(' ');
		            	//$('#validationMessage').text('');
				    
		            	//$('#progress .bar').fadeOut("slow");
		            	//$('#progress .bar').css(
	    	             //   'width',
	    	              //   '0%'
	    	            //);
		            }
		            if(data.length> 0) {
						alert(data.length);
		            }

		            //$('#uploadIcon').attr('src', '${request.getContextPath()}/images/public/amarok_share_green.png');
		            $('#uploadIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_green.png');

		            $('#uploadBar').hide();
					$('#uploadBar').css("background-color","green")
					$('#uploadBar').fadeIn("slow");
		            
		            $('#results').html("");
		            $('#results').append("<h2 style='text-align: left'>Validation Summary</h2>");
		            $('#results').append("Validated against " + data.result.summary.total + " rules: ");
		            fileName =  data.result.summary.file;
		            $('#results').append("<font style='display:inline;color:green;'>Passed: " + data.result.summary.pass + "</font>, ");
		            $('#results').append("<font style='display:inline;color:red;'>Errors: " + data.result.summary.error + "</font>, ");
		            $('#results').append("<font style='display:inline;color:#C09853;'>Warnings: " + data.result.summary.warn + "</font>, ");
		            $('#results').append("<font style='display:inline;color:#999999;'>Skipped: " + data.result.summary.skip + "</font><br/>");
		           
		            $('#results').append("<br/><br/>");

					var results = data.result.results;
					for(var s=0; s<results.length; s++) {
						var result = results[s];
						 if(result.error>0)
							 $('#results').append("<img src='${request.getContextPath()}/images/error.png' width='24px' style='display:inline;'/>");
						 else if(result.warn>0)
							 $('#results').append("<img src='${request.getContextPath()}/images/warning.png' width='24px' style='display:inline;'/>");
						 else if(result.passed>0)
							 $('#results').append("<img src='${request.getContextPath()}/images/check.png' width='24px' style='display:inline;'/>");
						 else $('#results').append("<img src='${request.getContextPath()}/images/skip.png' width='24px' style='display:inline;'/>");

						 $('#results').append("<div style='text-align: left; display:inline;color:black;font-size: 180%;'>Section " + result.section + "</div><br/>");
						 
						 $('#results').append("Section summary:");
				         $('#results').append("<font style='display:inline;color:green;'>Passed: " + result.pass + "</font>, ");
				         $('#results').append("<font style='display:inline;color:red;'>Errors: " + result.error + "</font>, ");
				         $('#results').append("<font style='display:inline;color:#C09853;'>Warnings: " + result.warn + "</font>, ");
				         $('#results').append("<font style='display:inline;color:#999999;'>Skipped: " + result.skip + "</font><br/>");						 
						 $('#results').append("<br/>");
						 
						 var constraints = result.constraints;
						 jQuery.each(constraints, function(j, constraint) {
							 if(constraint.status=='pass')
								 $('#results').append("<img src='${request.getContextPath()}/images/check.png' width='18px' style='display:inline;'/>" +
									"<font style='display:inline; font-weight: bold; font-size: 130%; color: green;'>" + constraint.ref + "</font><br/> ");
							 else if(constraint.status=='warn') {
								 $('#results').append("<img src='${request.getContextPath()}/images/warning.png' width='18px'style='display:inline;'/>"+
									"<font style='display:inline; font-weight: bold; font-size: 130%; color: #C09853;'>" + constraint.ref + "</font><br/> ");
								 $('#results').append(constraint.result);
							 } else if(constraint.status=='skip') {
								 $('#results').append("<img src='${request.getContextPath()}/images/skip.png' width='18px' style='display:inline;'/>"+
									"<font style='display:inline; font-weight: bold; font-size: 130%; color: #999999;'>" + constraint.ref + "</font><br/> ");
							 } else if(constraint.status=='error') {
								 $('#results').append("<img src='${request.getContextPath()}/images/skip.png' width='18px' style='display:inline;'/>"+
									"<font style='display:inline; font-weight: bold; font-size: 130%; color: red;'>" + constraint.ref + "</font><br/> ");
								 $('#results').append(constraint.result);
							}
							 
							 $('#results').append("<a href='" + constraint.url + "'>" + constraint.description + "</a><br/> ");
							 $('#results').append("<br/>");
						 });
						 $('#results').append("<br/>");
					};

					if(data.result.summary.error>0) {
						$('#validationIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_red.png');
						$('#validationMessage').text('Errors (see report below)');
						$('#validationBar').hide();
						$('#validationBar').css("background-color","red")
						$('#validationBar').fadeIn("slow");
						//$('#persistIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_red.png');
						$('#persistBar').hide();
						$('#persistBar').css("background-color","gray")
						$('#persistBar').fadeIn("slow");
						$('#persistMessage').text('Invalid content cannot be stored');
						$('#persistButtonPanel').hide();
					} else if(data.result.summary.warn>0) {
						$('#validationIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_orange.png');
						$('#validationMessage').text('Validated with warnings (see report below)');
						$('#validationBar').hide();
						$('#validationBar').css("background-color","orange")
						$('#validationBar').fadeIn("slow");
						//$('#persistIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_red.png');
						
						$('#persistBar').hide();
						$('#persistBar').css("background-color","gray")
						$('#persistBar').fadeIn("slow");
						$('#persistMessage').text('Wanna store the data?');
						$('#persistButtonPanel').fadeIn('slow');
					} else {
						$('#validationIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_green.png');
						$('#validationMessage').text('Validated!');
						$('#validationBar').hide();
						$('#validationBar').css("background-color","green")
						$('#validationBar').fadeIn("slow");
						//$('#persistIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_red.png');
						
						$('#persistBar').hide();
						$('#persistBar').css("background-color","gray")
						$('#persistBar').fadeIn("slow");
						$('#persistMessage').text('Not implemented yet...');
						$('#persistButtonPanel').fadeIn('slow');
					}
		        },
		        add: function (e, data) {
		        	data.context = '';
		            data.context = $('<p/>').text('Uploading...').appendTo('#message');
		            data.submit();
		        },
		        progressall: function (e, data) {
		            var progress = parseInt(data.loaded / data.total * 100, 10);
		            $('#progress .bar').css(
		                'width',
		                progress + '%'
		            );
		        },
		        error: function (e, data) {
		        	$('#message').html('');
		        	$('#uploadIcon').attr('src', '${request.getContextPath()}/images/public/amarok_share_red.png');
		        	$('#uploadBar').css("background-color","red");
		        	data.context = $('<p/>').text('Upload failed: '  + e.responseText).appendTo('#message');
		        	
		        	// $.each(data, function(key, element) {
		        	// 	alert('key: ' + key + '\n' + 'value: ' + element);
		        	 //});
		        }
		    })
		     .on('fileuploadadd', function (e, data) {
		    	 $('#message').text('');
		    	 $('#uploadIcon').attr('src', '${request.getContextPath()}/images/public/amarok_share.png');
		    	 $('#validateIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate.png');
		    	 $('#persistIcon').attr('src', '${request.getContextPath()}/images/public/amarok_download.png');
		     })
		});

		function persistAnnotation() {
			//alert(fileName);
			$.post('${request.getContextPath()}/Upload/persistAnnotationFile?fileName='+fileName, function(data) {
				$.post('${request.getContextPath()}/TripleStore/retrieveGraph?uri='+encodeURIComponent(data), function(data) {
					//alert(data);
					$('#persistBar').hide();
					$('#persistBar').css("background-color","gray")
					$('#persistBar').fadeIn("slow");
					$('#persistMessage').text('Annotation persisted!');
					$('#persistButtonPanel').hide();
					$('#persistIcon').attr('src', '${request.getContextPath()}/images/public/amarok_validate_green.png');
				});
			});
			
		}
	</script>
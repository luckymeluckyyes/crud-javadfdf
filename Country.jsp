<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="themes/js/InfiniteScroll/js/jquery-1.11.0.js"></script>
<script type="text/javascript" src="themes/js/InfiniteScroll/js/datatables.min.js"></script>
<script type="text/javascript" src="themes/js/InfiniteScroll/js/jquery.mockjax.min.js"></script>
<script type="text/javascript" src="themes/js/InfiniteScroll/js/datatables.mockjax.js"></script>
    
 <link rel="stylesheet" href="themes/js/InfiniteScroll/css/datatables.min.css">

<style>
.row {
	justify-content: center;
}
</style>

<script>
	var username = "${username}";
</script>

<form action="Country_action" modelAttribute="Country_cmd" method="post"
	class="form-horizontal">
	<div class="container" align="center">
		<div class="card">
			<div class="card-header title">
				<h5>
					<span id="lbladd"></span> ADD COUNTRY MASTER
				</h5>
			</div>
			<div class="card-body card-block">
				<div class="row">
					<div class="col-md-6 left_content">
						<div class="row form-group">
							<div class="col-md-6">
								<label for="username">COUNTRY<span class="star_design"></span></label>
							</div>
							<div class="col-md-6">
								<input id="country" name="country"
									class="form-control" autocomplete="off" maxlength="25"
									placeholder="Maximum 25 Character" />
								<div class="col-md-6">
									<input type="hidden" id="id" name="id" value="0" class="form-control"
										autocomplete="off" />
								</div>
							</div>
						</div>
					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-6 left_content">
						<div class="row form-group">
							<div class="col-md-6">
								<label for="username">STATUS<span class="star_design"></span></label>
							</div>
							<div class="col-md-6">
								<select name="status" id="status" class="form-control">
												<option value="1">Active</option>
												<option value="2">Inactive</option>
<%-- 									<c:forEach var="item" items="${getEducationStatusList}" varStatus="num"> --%>
<%-- 										<option value="${item[0]}" name="${item[1]}">${item[1]}</option> --%>
<%-- 									</c:forEach> --%>
								</select>
								<div class="col-md-6">
									<input type="hidden" id="id" name="id" value="0" class="form-control"
										autocomplete="off" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="card-footer" align="center">
				<a href="country_url" class="btn btn-success btn-sm">Reset</a> <input
					type="submit" class="btn btn-primary btn-sm" value="Save"
					onclick="return Validation();"> 
				   <i class="action_icons searchButton"></i><input type="button"
					class="btn btn-info btn-sm" id="btn-reload"  value="Search">
			</div>

		</div>
	</div>
</form>



<c:url value="Edit_Country" var="Edit_Country" />
	<form:form action="${Edit_Country}" method="post" id="updateForm"
		name="updateForm" modelAttribute="id1">
		<input type="hidden" name="id1" id="id1" value="0" />
</form:form>
	
<c:url value="deleteUrl" var="deleteUrl" />
<form:form action="${deleteUrl}" method="post" id="deleteForm"
	name="deleteForm" modelAttribute="id1">
	<input type="hidden" name="idd" id="idd" value="0" />
</form:form>

<div class="container">
		<table id="search_Country" class="display table no-margin table-striped  table-hover  table-bordered">
			<thead>
				<tr>
					<th align="center">Ser No</th>
					<th>Country</th>
					<th class="action">Action</th>
				</tr>
			</thead>
		</table>
	</div>

<script type="text/javascript">

$(document).ready(function() {

	if('${msg}' !="")
	{alert('${msg}')}
	
	mockjax1('search_Country');
	table = dataTable('search_Country');
	$('#btn-reload').on('click', function() {
		table.ajax.reload();
	});
	

});

function data(search_Country) {
	jsondata = [];
	var table = $('#' + search_Country).DataTable();
	var info = table.page.info();
//		var currentPage = info.page;
	var pageLength = info.length;
	var startPage = info.start;
	var endPage = info.end;
	var Search = table.search();
	var order = table.order();
	//var orderColunm = $(table.column(order[0][0]).header()).attr('id').toLowerCase();
	var orderColunm = $(table.column(order[0][0]).header()).html()
			.toLowerCase();
	var orderType = order[0][1];
	
	var country = $("#country").val();
	
	$.post("getCountry_data?" + key + "=" + value, {
		startPage : startPage,
		pageLength : pageLength,
		Search : Search,
		orderColunm : orderColunm,
		orderType : orderType,
		country:country
	}, function(j) {

		for (var i = 0; i < j.length; i++) {
			jsondata.push([ i + 1, j[i].country,j[i].action ]);
		}
	});
	$.post("getCountry_dataCount?" + key + "=" + value, {
		country:country
	}, function(j) {
		FilteredRecords = j;
	});
}


function editData(id) {
	$("#id1").val(id);
	//alert(id);
	document.getElementById('updateForm').submit();
}
function deleteData(id) {
	debugger;
	$("#idd").val(id);
	document.getElementById('deleteForm').submit();
}

function Validation(){
	
	if ($("input#country").val().trim() == "") {
		alert("Please Enter country");
		$("input#country").focus();
		return false;
	}
	if ($("select#status").val() == "2") {
		alert("Only Select Active Status");
		$("select#status").focus();
		return false;
	}
}

</script>

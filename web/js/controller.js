var app = angular.module('MyApp', ['ngAnimate', 'ngSanitize', 'ui.bootstrap']);

app.controller("MainController", function($scope, $sce, $http, $uibModal, $window){

	var domain = "/drive/";

	$scope.init = true;
	$scope.username = "";
	$scope.usernameSU = "";
	$scope.storageSize = 0;
	$scope.MyDriveVisible = true;

	$scope.user = {};

	$scope.showed_fs = [];
	$scope.visited_fs = [];
	$scope.checked = [];

	$scope.multipleOptions = false;
	$scope.selecting = false;
	$scope.usernameToShare = "";

	$scope.usernameOwner = "";
	$scope.pathLink = "";

	$scope.currentPath = "/";

	$scope.forced = false;
	$scope.directoryName = "";
	$scope.fileName = "";
	$scope.fileExtention = "";
	$scope.content = "";

	$scope.logOut = function(){
		$window.location.reload();
	}

	$scope.setDefaultProp = function(){
		return "<h4>Details</h4>Select a file or a folder to see its details.";
	}

	$scope.properties = $scope.setDefaultProp();

	$scope.destination = "/";

	$scope.getDirectory = function(path, tree){
		if(path === "/"){
			return tree;
		}

		let parts = path.split("/");

		for (i in tree)
		{
			if(tree[i].type == "Directory" &&
				tree[i].name.fsName == parts[1] &&
				parts.length == 3
			){
				return tree[i].tree;
			}
			else if(tree[i].type == "Directory" &&
				tree[i].name.fsName == parts[1]
			){
				let newPath = path.slice(parts[1].length+1);
				return $scope.getDirectory(newPath,tree[i].tree);
			}
		}
	}

	// Login

	$scope.login = function(change){
		$http({
			url: domain+'enter',
			method: "POST",
			data: { username : $scope.username }
			})
		.then(function(response) {

			if(response.data.status == 1){
				
				if(change == true){
					changeView();
				}
				$scope.user = response.data.account;
				if($scope.MyDriveVisible == true)
					$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.myDrive);
				else
					$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.shared);
				$scope.showed_fs = $scope.showed_fs.sort(function(a, b) {
					return a.name.fsName.localeCompare(b.name.fsName);
				});
			}
			else if(response.data.status == 2){
				bootbox.alert("The user doesn't exist.");
			}
			else{
				console.log("Error interno @login");
			}
		}, 
		function(response) {
		});
	}

	$scope.signUp = function(){
		$http({
			url: domain+'create',
			method: "POST",
			data: { username : $scope.usernameSU, sizeStorage: $scope.storageSize }
			})
		.then(function(response) {

			if(response.data.status == 1){
				$scope.username = $scope.usernameSU;
				$scope.login(true);

			}
			else if(response.data.status == 2){
				bootbox.alert("The user already exists.");
			}
			else{
				console.log("Error interno @signUp");
			}
		}, 
		function(response) {
		});
	}

	function changeView(){
		$scope.init = !$scope.init;
	}


	$scope.showPropertiesFS = function(file){
		if (file.type == "Directory"){
			let contentDirectory = "<h4>Detalles</h4><table class=\"table table-condensed\"><tbody><tr><th>Name</th><td>"+ file.name.fsName
			+"</td></tr><tr><th>Path</th><td>"+file.route.slice(8)
			+"</td></tr><tr><th>Size</th><td>"+file.size
			+"</td></tr><tr><th>Created date</th><td>"+ moment(new Date(parseInt(file.createDate))).format('MMMM Do YYYY, h:mm:ss a')
			+"</td></tr><tr><th>Modified date</th><td>"+ moment(new Date(parseInt(file.modifiedDate))).format('MMMM Do YYYY, h:mm:ss a')
			+"</td></tr><tr><th>Type</th><td>"+file.type
			+"</td></tr><tr><th>ID</th><td>"+file.id
			+"</td></tr></tbody></table>"

			$scope.properties = contentDirectory;
		}else if(file.type == "File"){
			let contentFile = "<h4>Detalles</h4><table class=\"table table-condensed\"><tbody><tr><th>Name</th><td>"+ file.name.fsName + "." + file.name.extension
			+"</td></tr><tr><th>Path</th><td>"+file.route.slice(8)
			+"</td></tr><tr><th>Size</th><td>"+file.size
			+"</td></tr><tr><th>Created date</th><td>"+ moment(new Date(parseInt(file.createDate))).format('MMMM Do YYYY, h:mm:ss a')
			+"</td></tr><tr><th>Modified date</th><td>"+ moment(new Date(parseInt(file.modifiedDate))).format('MMMM Do YYYY, h:mm:ss a')
			+"</td></tr><tr><th>Type</th><td>"+file.type
			+"</td></tr><tr><th>ID</th><td>"+file.id
			+"</td></tr></tbody></table>"
			$scope.properties = contentFile;
		}
	}

	$scope.enterFS = function(file){
		if (file.type == "Directory"){
			$scope.visited_fs.push($scope.currentPath);
			
			if($scope.MyDriveVisible == true){
				$scope.currentPath = file.route.slice(8);
				$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.myDrive);
			}
			else{
				$scope.currentPath = file.route.slice(7);
				$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.shared);
			}
			$scope.showed_fs = $scope.showed_fs.sort(function(a, b) {
				return a.name.fsName.localeCompare(b.name.fsName);
			});
		}else if(file.type == "File"){
			$scope.fileContent(file);
		}
		$scope.checked = [];
		$scope.multipleOptions = false;
		$scope.selecting = false;
		$scope.properties = $scope.setDefaultProp();
	}

	$scope.return = function(){
		if($scope.visited_fs.length > 0){
			$scope.currentPath = $scope.visited_fs.pop();
			if($scope.MyDriveVisible == true)
				$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.myDrive);
			else
				$scope.showed_fs = $scope.getDirectory($scope.currentPath, $scope.user.shared);
			$scope.showed_fs = $scope.showed_fs.sort(function(a, b) {
				return a.name.fsName.localeCompare(b.name.fsName);
			});
		}
		$scope.checked = [];
		$scope.multipleOptions = false;
		$scope.selecting = false;
		$scope.properties = $scope.setDefaultProp();
	}

	$scope.toggleSelectionFS = function(fileRoute){
		var index = $scope.checked.indexOf(fileRoute);
		if (index > -1){
			$scope.checked.splice(index, 1);
			if($scope.checked.length == 0){
				$scope.selecting = false;
				$scope.multipleOptions = false;
			}
		}
		else{
			$scope.checked.push(fileRoute);
			$scope.selecting = true;
			$scope.multipleOptions = true;
		}
		console.log($scope.checked);
	}

	$scope.getPropertiesFS = function(file){
		var fileImage = "";
		var fileInfo = "";

		if (file.type == "File"){
			fileImage = "<p style='float: left;'>"
				+ "<img src='images/file_icon.png' height='50px'"
				+ "width='50px'></p>";
			fileInfo += file.name.fsName +"."+ file.name.extension + "<br>";
		} else {
			if (file.tree.length > 0){
				fileImage = "<p style='float: left;'>"
					+ "<img src='images/folder_icon2.png' height='50px'"
					+ "width='50px'></p>";
			} else {
				fileImage = "<p style='float: left;'>"
					+ "<img src='images/folder_icon.png' height='50px'"
					+ " width='50px'></p>";
			}
			fileInfo += file.name.fsName + "<br>";
		}

		//Adding Image
		fileInfo = fileImage + "<p>"+fileInfo+"</p>";

		return $sce.trustAsHtml(fileInfo);
	}


	$scope.isFile = function(fs){
		return fs.type == 'File'
	}

	$scope.isDirectoryEmpty = function(fs){
		return fs.type == 'Directory' && fs.tree.length == 0
	}

	$scope.isDirectoryFull = function(fs){
		return fs.type == 'Directory' && fs.tree.length > 0
	}


	$scope.moveAux = function(){

		if($scope.checked.length >=1){
			let path = $scope.checked.pop().slice(8);

			$http({
				url: domain+'move',
				method: "POST",
				data: {
					username: $scope.username,
					source: path,
					destination: $scope.destination,
					forced: $scope.forced
				}
				})
			.then(function(response) {

				if(response.data.status == 1){
					
					if($scope.checked.length != 0){
						$scope.forced = false;
						$scope.moveAux();
					}
					else{
						$scope.destination = "";
						$scope.login(false);
						$scope.multipleOptions = false;
						$scope.selecting = false;
						$scope.cleanFileVars();
					}

				}
				else if(response.data.status == 2){
					bootbox.alert(response.data.message);
				}
				else if(response.data.status == 5){
					bootbox.confirm("Do you want overwrite the "+path+" ?", 
						function(result){ 
							if(result == true){
								$scope.forced = true;
								$scope.checked.push("/myDrive"+path);
								$scope.moveAux();
							}else{
								$scope.moveAux();
							}
					});
				}
				else{
					console.log("Error interno@move");
				}
			}, 
			function(response) {
			});
		}
	}

	$scope.move = function(){
		bootbox.prompt("Select the destination path", function(result){
			console.log(result);
			if( /^\/([^\/]+\/)*$/i.test(result) ){
				$scope.destination = result;
				$scope.moveAux();
			}
			else if(result!= null){
				bootbox.alert("Invalid destination path");
			}
		});		
	}	

	$scope.copyAux = function(){

		if($scope.checked.length >=1){
			let path = "";
			let URL = "";
			if($scope.MyDriveVisible){
				path = $scope.checked.pop().slice(8);
				URL = domain+'copy';
			}else{
				path = $scope.checked.pop().slice(7);
				URL = domain+'copyS';
			}

			$http({
				url: URL,
				method: "POST",
				data: {
					username: $scope.username,
					source: path,
					destination: $scope.destination,
					forced: $scope.forced
				}
				})
			.then(function(response) {

				if(response.data.status == 1){
					
					if($scope.checked.length != 0){
						$scope.forced = false;
						$scope.copyAux();
					}
					else{
						$scope.destination = "";
						$scope.login(false);
						$scope.multipleOptions = false;
						$scope.selecting = false;
						$scope.cleanFileVars();
					}

				}
				else if(response.data.status == 2){
					bootbox.alert(response.data.message);
				}
				else if(response.data.status == 5){
					bootbox.confirm("Do you want overwrite the "+path+" ?", 
						function(result){ 
							if(result == true){
								$scope.forced = true;
								$scope.checked.push("/myDrive"+path);
								$scope.copyAux();
							}else{
								$scope.copyAux();
							}
					});
				}
				else{
					console.log("Error interno@move");
				}
			}, 
			function(response) {
			});
		}
	}

	$scope.copy = function(){
		bootbox.prompt("Select the destination path", function(result){
			console.log(result);
			if( /^\/([^\/]+\/)*$/i.test(result) ){
				$scope.destination = result;
				$scope.copyAux();
			}
			else if(result!= null){
				bootbox.alert("Invalid destination path");
			}
		});	
	}

	$scope.shareAux = function(){
		let path = $scope.checked.pop().slice(8);
		
		let tempPath= path;

		if(path.lastIndexOf("/")+1 === path.length){ // If is folder
			tempPath = path.substring(0,path.length-1)
		}


		$http({
			url: domain+'share',
			method: "POST",
			data: {
				username: $scope.username,
				source: path,
				usernameToShare: $scope.usernameToShare,
				nameFS: tempPath.substring(tempPath.lastIndexOf("/")+1)
			}
			})
		.then(function(response) {

			if(response.data.status == 1){
				
				if($scope.checked.length != 0){
					$scope.shareAux();
				}
				else{
					$scope.usernameToShare = "";
					$scope.login(false);
					$scope.multipleOptions = false;
					$scope.selecting = false;
					$scope.cleanFileVars();
				}

			}
			else if(response.data.status == 3){
				bootbox.alert(response.data.message);
			}
			else{
				console.log("Error interno@share");
			}
		}, 
		function(response) {
		});
	}

	$scope.share = function(){
		bootbox.prompt("Select the username to share.", function(result){
			console.log(result);
			if( result != null  && result != ""){
				$scope.usernameToShare = result;
				$scope.shareAux();
			}
			else{
				bootbox.alert("Invalid username empty");
			}
		});	
	}


	$scope.copyAuxRV = function(source){

		let DATA = {};

		if($scope.MyDriveVisible){
			DATA = {
				username: $scope.username,
				source: source,
				destination: $scope.destination,
				forced: $scope.forced
			};
		}
		else{
			DATA = {
				username: $scope.username,
				source: source,
				destination: $scope.destination,
				forced: $scope.forced,
				shared: true
			};
		}

		$http({
			url: domain+'copyRV',
			method: "POST",
			data: DATA
			})
		.then(function(response) {

			if(response.data.status == 1){
				
				$scope.destination = "";
				$scope.login(false);
				$scope.multipleOptions = false;
				$scope.selecting = false;
				$scope.cleanFileVars();

			}
			else if(response.data.status == 2){
				bootbox.alert(response.data.message);
			}
			else if(response.data.status == 5){
				console.log("Not forced");
			}
			else{
				console.log("Error interno@copyRV");
			}
		}, 
		function(response) {
		});
	}

	$scope.copyRV = function(){
		bootbox.prompt("Select the origin path", function(result){
			if( /^[cC]:[/\\]([^/\\]+[/\\]?)*$/i.test(result)){
				$scope.destination = $scope.currentPath;
				$scope.copyAuxRV(result);
			}
			else if(result!= null){
				bootbox.alert("Invalid destination path");
			}
		});	
	}

	$scope.copyAuxVR = function(){
		let path = "";

		let DATA = {};

		if($scope.MyDriveVisible){
			path = $scope.checked.pop().slice(8);
			DATA = {
				username: $scope.username,
				source: path,
				destination: $scope.destination
			}
		}
		else{
			path = $scope.checked.pop().slice(7);
			DATA = {
				username: $scope.username,
				source: path,
				destination: $scope.destination,
				shared: true
			}
		}

		$http({
			url: domain+'copyVR',
			method: "POST",
			data: DATA
			})
		.then(function(response) {

			if(response.data.status == 1){
				
				if($scope.checked.length != 0){
					$scope.copyAuxVR();
				}
				else{
					$scope.destination = "";
					$scope.login(false);
					$scope.multipleOptions = false;
					$scope.selecting = false;
					$scope.cleanFileVars();
				}

			}
			else if(response.data.status == 2){
				bootbox.alert(response.data.message);
			}
			else{
				console.log("Error interno@copyVR");
			}
		}, 
		function(response) {
		});
	}

	$scope.copyVR = function(){
		bootbox.prompt("Select the destination path", function(result){
			if(/^[cC]:[/\\]([^/\\]+[/\\])*$/i.test(result) ){
				$scope.destination = result;
				$scope.copyAuxVR();
			}
			else if(result!= null){
				bootbox.alert("Invalid destination path");
			}
		});	
	}

	$scope.delete = function(){
		let path = "";
		let DATA = {};

		if($scope.MyDriveVisible){
			path = $scope.checked.pop().slice(8);
			DATA = {
				username: $scope.username,
				source: path
			};
		}else{
			path = $scope.checked.pop().slice(7);
			DATA = {
				username: $scope.username,
				source: path,
				shared: true
			};
		}

		$http({
			url: domain+'delete',
			method: "POST",
			data: DATA
			})
		.then(function(response) {

			if(response.data.status == 1){
				
				if($scope.checked.length != 0){
					$scope.delete();
				}
				else{
					$scope.login(false);
					$scope.multipleOptions = false;
					$scope.selecting = false;
				}

			}
			else if(response.data.status == 2){
				bootbox.alert(response.data.message);
			}
			else{
				console.log("Error interno@delete");
			}
		}, 
		function(response) {
		});
	}

	$scope.createFS = function(type, update){

		let URL = "";
		let DATA = {};

		if(type === 'directory'){
			URL = domain+'createDirectory';
			DATA = { username : $scope.username, 
				path: $scope.currentPath, 
				forced: $scope.forced, 
				directoryName: $scope.directoryName };
		}
		else if(type === 'file' && update === true){
			URL = domain+'updateFile';

			if($scope.MyDriveVisible == true){
				DATA = { username : $scope.username, 
					path: $scope.currentPath, 
					forced: $scope.forced, 
					fileName: $scope.fileName,
					fileExtention: $scope.fileExtention,
					content: $scope.content}
			}else{
				DATA = { username : $scope.usernameOwner, 
					path: $scope.pathLink, 
					forced: $scope.forced, 
					fileName: $scope.fileName,
					fileExtention: $scope.fileExtention,
					content: $scope.content}
			}
		}
		else{
			URL = domain+'createFile';
			DATA = { username : $scope.username, 
					path: $scope.currentPath,  
					forced: $scope.forced, 
					fileName: $scope.fileName,
					fileExtention: $scope.fileExtention,
					content: $scope.content}
		}

		$http({
			url: URL,
			method: "POST",
			data: DATA
			})
		.then(function(response) {

			if(response.data.status == 1){
				$scope.login(false);
				$scope.cleanFileVars();
			}
			else if(response.data.status == 2){
				bootbox.confirm("Do you want overwrite the "+((type=="directory")?"folder":"file")+"?", 
					function(result){ 
						if(result == true){
							$scope.forced = true;
							$scope.createFS(type, false);
						}else{
							bootbox.alert("The "+((type=="directory")?"folder":"file")+" can't be created.");
						}
				});

			}
			else if(response.data.status == 4){
				bootbox.alert(response.data.message);
			}
			else{
				console.log("Error interno");
			}
		}, 
		function(response) {
		});
	}
	
	$scope.addDirectory = function() {

		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'ModalNewDirectory.html',
			controller: 'ModalNewDirectory',
			controllerAs: '$scope',
			resolve:{
			}
		});

		modalInstance.result.then(function (data) {
			$scope.forced = data.forced;
			$scope.directoryName = data.directoryName;
			$scope.createFS('directory', false);

		}, function () {
			console.log('Modal dismissed at: ' + new Date());
		});
	};

	$scope.addFile = function() {
		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'ModalNewFile.html',
			controller: 'ModalNewFile',
			controllerAs: '$scope',
			resolve:{
			}
		});

		modalInstance.result.then(function (data) {
			$scope.fileName = data.fileName;
			$scope.fileExtention = data.fileExtention;
			$scope.content = data.content;
			$scope.createFS('file', false);

		}, function () {
			console.log('Modal dismissed at: ' + new Date());
		});
	};

	$scope.fileContent = function(file) {
		

		var modalInstance = $uibModal.open({
			animation: true,
			ariaLabelledBy: 'modal-title',
			ariaDescribedBy: 'modal-body',
			templateUrl: 'ModalContentFile.html',
			controller: 'ModalContentFile',
			controllerAs: '$scope',
			resolve:{
				fileFullName: function(){
					return file.name.fsName + "." + file.name.extension;
				},
				content: function(){
					return file.content;
				}
			}
		});

		modalInstance.result.then(function (data) {

			if(file.hasOwnProperty("owner")){
				$scope.usernameOwner = file.owner;
				$scope.pathLink = file.pathLink;
			}

			$scope.forced = true;
			$scope.fileName = data.fileName;
			$scope.fileExtention = data.fileExtention;
			$scope.content = data.content;
			$scope.createFS('file', true);

		}, function () {
			console.log('Modal dismissed at: ' + new Date());
		});
	};

	$scope.cleanFileVars = function() {
		$scope.forced = false;
		$scope.fileName = "";
		$scope.fileExtention = "";
		$scope.content = "";
		$scope.directoryName = "";
	}

	$scope.home = function(){
		$scope.currentPath = "/";
		$scope.showed_fs =  $scope.getDirectory($scope.currentPath, $scope.user.myDrive);
		$scope.showed_fs = $scope.showed_fs.sort(function(a, b) {
			return a.name.fsName.localeCompare(b.name.fsName);
		});
		$scope.checked = [];
		$scope.multipleOptions = false;
		$scope.selecting = false;
		$scope.properties = $scope.setDefaultProp();
		$scope.MyDriveVisible = true;
		document.getElementById("SS").classList.remove("widebuttonActive");
		document.getElementById("MD").classList.add("widebuttonActive");
	}

	$scope.getCurrentPath = function(){
		var path = "";

		if($scope.currentPath === "/" && $scope.MyDriveVisible == true){
			return "MyDrive";
		}
		else if($scope.currentPath === "/" && $scope.MyDriveVisible == false){
			return "Shared";
		}

		if($scope.MyDriveVisible == true){
			path = "MyDrive";
		}
		else{
			path = "Shared";
		}
		
		let folders = $scope.currentPath.split("/");
		folders = folders.slice(1, folders.length-1);

		for (index in folders)
		{
			path += " > " + folders[index];
		}

		return path;
	}

	$scope.showShared = function(){
		$scope.MyDriveVisible = false;
		$scope.currentPath = "/";
		$scope.showed_fs =  $scope.getDirectory($scope.currentPath, $scope.user.shared);
		$scope.showed_fs = $scope.showed_fs.sort(function(a, b) {
			return a.name.fsName.localeCompare(b.name.fsName);
		});
		$scope.checked = [];
		$scope.multipleOptions = false;
		$scope.selecting = false;
		$scope.properties = $scope.setDefaultProp();
	}
});

app.controller('ModalNewDirectory', function ($scope, $uibModalInstance) {

	$scope.directoryName = "";

	$scope.ok = function () {
		let data = {
			directoryName: $scope.directoryName
		}
		$uibModalInstance.close(data);
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});

app.controller('ModalNewFile', function ($scope, $uibModalInstance) {

	$scope.fileFullName = "";
	$scope.content = "";

	$scope.ok = function () {

		if(/.+\..+$/i.test($scope.fileFullName)){
			let data = {
				fileName: $scope.fileFullName.split(".")[0],
				fileExtention: $scope.fileFullName.split(".")[1],
				content: $scope.content
			}
			$uibModalInstance.close(data);
		}
		else{
			bootbox.alert("File name (without extension) is invalid.")
		}
		
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});


app.controller('ModalContentFile', function ($scope, $uibModalInstance, fileFullName, content) {

	$scope.edit = false;
	$scope.fileFullName = fileFullName;
	$scope.content = content;
	$scope.contentEdited = content;

	$scope.ok = function () {
		let data = {
			forced: true,
			fileName: $scope.fileFullName.split(".")[0],
			fileExtention: $scope.fileFullName.split(".")[1],
			content: $scope.contentEdited
		}
		$uibModalInstance.close(data);
	};

	$scope.cancel = function () {
		$uibModalInstance.dismiss('cancel');
	};
});

app.filter('fileNameFilter', function() {

	return function(file) {

		if(file.type == "File"){
			return file.name.fsName +"."+ file.name.extension;
		}
		else{
			return file.name.fsName
		}
	}

});

app.directive('focusInputSs', function() {
	return {
		link: function(scope, element, attrs) {
			element.bind('click', function() {
				document.getElementById("MD").classList.remove("widebuttonActive");
				element.addClass("widebuttonActive");
			});
		}
	};
});

app.directive('focusInputMd', function() {
	return {
		link: function(scope, element, attrs) {
			element.bind('click', function() {
				document.getElementById("SS").classList.remove("widebuttonActive");
				element.addClass("widebuttonActive");
			});
		}
	};
});
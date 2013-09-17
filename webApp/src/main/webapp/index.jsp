<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>Ember.js • </title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
  <script type="text/x-handlebars" >
    <div class="title">
		<h2>Sample Ember JS Data</h2>
		<p>Employee details</p>
		<div>{{#linkTo 'empadd'}}Add New record{{/linkTo}}</div>
	</div>   
	{{outlet}}
  </script>
  <script type="text/x-handlebars" id="emp">
           <table>
				<tr>
					 <th>First name</th>
					 <th>Last name</th>
					 <th>City</th>
					 <th>State</th>
					 <th>PIN</th>
					 <th colspan=2>Action</th>
				</tr>
            {{#each controller itemController="Employee"}}
            <tr><td>{{fname}}</td><td>{{lname}}</td><td>{{city}}</td><td>{{state}}</td><td>{{pin}}</td><td>{{#linkTo 'empedit' this}}Edit{{/linkTo}}</td><td><button {{action deleteRec}}>Delete</button></td></tr>
             {{/each}}
			</table>
     
        
  </script>
  <script  type="text/x-handlebars" id="empadd">   
	 <div class="controls">
		
		{{view Ember.TextField id="fname" placeholder="First Name"
       valueBinding="fname" }}
		{{view Ember.TextField id="lname" placeholder="Last Name"
       valueBinding="lname" }}
       {{view Ember.TextField id="city" placeholder="City"
       valueBinding="city" }}
       {{view Ember.TextField id="state" placeholder="state"
       valueBinding="state" }}
       {{view Ember.TextField id="pin" placeholder="pin"
       valueBinding="pin" }}	 
       <button  id="add" {{action addRec}}>Add record</button>	 
      
		</div>
	</script>	
  <script  type="text/x-handlebars" id="empedit">
  
  <div class="controls">
		
		{{view Ember.TextField id="fname" placeholder="First Name"
       valueBinding="fname" }}
		{{view Ember.TextField id="lname" placeholder="Last Name"
       valueBinding="lname" }}
       {{view Ember.TextField id="city" placeholder="City"
       valueBinding="city" }}
       {{view Ember.TextField id="state" placeholder="state"
       valueBinding="state" }}
       {{view Ember.TextField id="pin" placeholder="pin"
       valueBinding="pin" }}	 
       
      <button  id="update" {{action updateRec}}>Update record</button>
		</div>
  </script>
  <script src="js/jquery.js"></script>
  <script src="js/handlebars.js"></script>
  <script src="js/ember.js"></script>
  <script src="js/ember-data.js"></script>
  <script src="js/lsAdapter.js"></script>
  <script src="js/app.js"></script>
</body>
</html>
import React from "react";
import { Switch, Route } from "react-router-dom";
import Home from "./Home";
// import Data from "./Data";
import Data from "./datainstance/DataList";

const App = () => (
  <div class="container">


      <nav class="navbar navbar-default">
        <div class="container-fluid">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Search Studio</a>
          </div>
          <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav navbar-right">
              <li class="active"><a href="./">Default <span class="sr-only">(current)</span></a></li>
              <li><a href="../navbar-static-top/">Static top</a></li>
              <li><a href="../navbar-fixed-top/">Fixed top</a></li>
            </ul>
          </div>
        </div>
      </nav>

      
      <div class="jumbotron">
      <Switch>
        <Route exact path="/" component={Home} />
        <Route exact path="/data/:schemaId" component={Data} />
      </Switch>
      </div>

    </div>
)

export default App

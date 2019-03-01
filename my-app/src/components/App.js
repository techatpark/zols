import React from "react";
import { Link,Switch, Route } from "react-router-dom";
import SchemaList from "./schema/SchemaList";
import DataList from "./datainstance/DataList";
import Data from "./datainstance/Data";

const App = () => (
  <div className="container">


      <nav className="navbar navbar-default">
        <div className="container-fluid">
          <div className="navbar-header">
            <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span className="sr-only">Toggle navigation</span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
            </button>
            <Link className="navbar-brand" to={`/`}>Search Studio</Link>
          </div>
          <div id="navbar" className="navbar-collapse collapse">
            <ul className="nav navbar-nav navbar-right">
              <li className="active"><a href="./">Default <span className="sr-only">(current)</span></a></li>
              <li><a href="../navbar-static-top/">Static top</a></li>
              <li><a href="../navbar-fixed-top/">Fixed top</a></li>
            </ul>
          </div>
        </div>
      </nav>

      
      <div className="jumbotron">
      <Switch>
        <Route exact path="/" component={SchemaList} />
        <Route exact path="/data/:schemaId" component={DataList} />
        <Route exact path="/data/:schemaId/*" component={Data} />
      </Switch>
      </div>

    </div>
)

export default App

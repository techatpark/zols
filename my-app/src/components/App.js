import React, { Component } from "react";
import { Link,Switch, Route } from "react-router-dom";
import SchemaList from "./schema/SchemaList";
import Schema from "./schema/Schema";
import DataList from "./datainstance/DataList";
import Data from "./datainstance/Data";
import Api from "../api";

export default class App extends Component {

  state = {
    locale: "English"
  };


  onLocale = (e) => {
    e.preventDefault();
    this.setState({ locale: e.target.innerHTML});
    Api.setLocale(e.target.getAttribute("data-locale"));
  };

  render(){
  return <div className="container-fluid">


      <nav className="navbar navbar-default">
        <div className="container-fluid">
          <div className="navbar-header">
            <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span className="sr-only">Toggle navigation</span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
              <span className="icon-bar"></span>
            </button>
            <div className="navbar-left logo">
            <Link className="navbar-brand" to={`/`}><svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 100 100"><path fill="#3DB39E" d="M50 0c-27.614 0-50 22.386-50 50s22.386 50 50 50 50-22.386 50-50-22.386-50-50-50z"/><path fill="#37A18E" d="M79.222 72.675c-1.415-1.415-3.45-1.802-5.225-1.199l-4.722-4.723c3.365-4.424 5.367-9.942 5.367-15.931 0-14.546-11.792-26.338-26.339-26.338-14.546 0-26.338 11.792-26.338 26.338 0 14.547 11.792 26.339 26.338 26.339 5.988 0 11.507-2.002 15.931-5.368l4.723 4.723c-.602 1.773-.215 3.81 1.199 5.224l8.923 8.923c3.492-2.502 6.655-5.435 9.396-8.733l-9.253-9.255z"/><path fill="#F5F5F5" d="M68.535 63.464l-5.07 5.071 19.255 19.255c1.809-1.567 3.503-3.263 5.071-5.071l-19.256-19.255z"/><path fill="#fff" d="M48.5 22c14.636 0 26.5 11.864 26.5 26.5 0 14.635-11.864 26.5-26.5 26.5s-26.5-11.865-26.5-26.5c0-14.636 11.864-26.5 26.5-26.5z"/><path fill="#82CEE8" d="M48.5 27c11.874 0 21.5 9.626 21.5 21.5s-9.626 21.5-21.5 21.5-21.5-9.626-21.5-21.5 9.626-21.5 21.5-21.5z"/><path fill="#A8DDEF" d="M38.189 53.379c-.499-.5-1.31-.5-1.81 0s-.5 1.311 0 1.811l5.431 5.43c.5.5 1.31.5 1.81 0 .5-.499.5-1.31 0-1.81l-5.431-5.431zm11.435 4.45l-10.454-10.454c-.495-.495-1.299-.495-1.794 0s-.495 1.299 0 1.794l10.454 10.454c.495.495 1.299.495 1.794 0s.495-1.298 0-1.794z"/><path fill="#EFC75E" d="M79.606 70.485c-1.996-1.996-5.232-1.996-7.229 0l-1.892 1.892c-1.996 1.997-1.996 5.233 0 7.229l10.007 10.007c3.419-2.636 6.485-5.702 9.121-9.121l-10.007-10.007z"/><path fill="#D7B354" d="M72.507 77.606c-1.993-1.993-1.995-5.223-.008-7.22l-.121.099-1.892 1.892c-1.996 1.997-1.996 5.233 0 7.229l10.007 10.007c.755-.582 1.491-1.187 2.211-1.81l-10.197-10.197z"/><path fill="#72B5CC" d="M48.5 27c-6.701 0-12.684 3.067-16.627 7.873 3.71-3.044 8.454-4.873 13.627-4.873 11.874 0 21.5 9.626 21.5 21.5 0 5.173-1.829 9.917-4.873 13.627 4.806-3.943 7.873-9.926 7.873-16.627 0-11.874-9.626-21.5-21.5-21.5z"/></svg>
            </Link>
            <ul className="nav navbar-nav navbar-right">
            <li><a href="/">Search Studio</a></li>
            </ul>
            </div>
          </div>
          <div id="navbar" className="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                {this.state.locale} <span class="caret"></span>
              </a>
              <ul class="dropdown-menu">
                <li ><a href="javascript://" data-locale="zh" onClick={this.onLocale}>Chineese</a></li>
                <li><a href="javascript://" data-locale="" onClick={this.onLocale}>English</a></li>
              </ul>
            </li>

          </ul>
          </div>
        </div>
      </nav>


      <div className="container-fluid">
      <Switch>
        <Route exact path="/" component={SchemaList} />
          <Route exact path="/schema/:schemaId" component={Schema} />
          <Route exact path="/schema/:schemaId/_addNew" component={Schema} />
        <Route exact path="/data/:schemaId" component={DataList} />
        <Route exact path="/data/:schemaId/*" component={Data} />
      </Switch>
      </div>

    </div>
  }
}

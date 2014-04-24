Zols
====

Zols is a Light weighted Content Management System built on Spring Framework. Apart from providing anything you want from a CMS, Zols is unique in three ways:

#CMS is just a Library
Unlike many other CMS which are webapplication, Zols is just a library which can be easily integrated with any of your existing Spring MVC Application. There is no hassle of handling a separate deployment. 

#From HTML to CMS in no time
Zols uses strict natural templating. HTML Projects can be directly linked to CMS. Designers are free to modify HTML files which will reflect in real time within the CMS.

#Native Data Format
Content is no longer just a text. Any data we enter into zols is stored in it's native format. For E.g Dates, Time, Number etc. Which will provide the posiblity of addtional search capablities. (like serach pages by date range etc)

Installation:
==========

As mentioned above, you don't need to run zols as web application. You can include zols into your Spring application easily. Detailed sample is available @ https://github.com/sathishk/zols-sample.

For testing purpose, you can run zols as given below in the steps section.

Prerequisites
-------------
JDK 1.7

Maven

MongoDB

Git

Steps:
---------------

1) Start a mongo server as documented in the mongo website. 

2) `git clone https://github.com/sathishk/zols.git`

3) `cd zols`

4) From within the `zols` folder, run:

5) `mvn clean install`

6) `cd webApp`

7) `mvn tomcat7:run`

That is all. You can access the application @ `http://localhost:8080/zols`

Login to the application using the username/password as "admin" and
"admin".


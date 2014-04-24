Zols
====

Zols is a Light weighted Content Management System built on Spring Framework. Apart from providing anything you want from a CMS, Zols is unique in three ways:

# Pluggable CMS
Zols is not a web application.
It is a library which can be easily integrated with any existing Spring MVC Application. 
This means that this saves the hassle of handling a separate deployment.

#From HTML to CMS in no time
Zols uses strict natural templating. HTML Projects can be directly linked to CMS. Designers are free to modify HTML files which will reflect in real time within the CMS.

#Store domain and data
Dates as dates, and SSN's as SSN's. Store them in the format that you
want to be saved, and extract the data with the domain.
This means that you can perform custom queries like searching within
date ranges or searching for a range of ZIP codes in your CMS naturally
without any custom coding. 



Installation:
==========

As mentioned above you don't need to run zols as web application. You can include zols into your Spring application easily. Detailed sample is available @ https://github.com/sathishk/zols-sample.

Anyways, For testing purpose, you can run zols as given below in the
steps section.

Prerequisites
-------------
JDK 1.7

Maven

MongoDB

Git

Steps:
---------------

1)  `git clone https://github.com/sathishk/zols.git`

2) `cd zols`

3) Start a mongo server as documented in the mongo website. Create a
database named 'zols' within the mongo instance.

4) From within the `zols` folder, run:

5) `mvn clean install`

6) `cd webApp`

7) `mvn tomcat7:run`

That is all. You can access the application @ `http://localhost:8080/zols`

Login to the application using the userid / password as "admin" and
"admin".


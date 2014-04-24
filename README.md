Zols
====

Zols is a Light weighted Content Management System built on Spring Framework. Apart from providing anything you want from a CMS, Zols is unique in three ways

(1) Zols is not a web application. It is a library which can be easily integrated with any existing Spring MVC Application. NO SEPARATE DEPLOYMENT !!

(2) Zols uses strict natural templating. HTML Projects can be directly linked to CMS. Designers are free to modify HTML files which will reflect in real time @ CMS.

(3) Clear Separation of data. Anything you enter into the system is native data which can be separately processed and analyzed.


Installation:
==========

As mentioned above you dont need to run zols as web application. You can include zols into your Spring application easily. Detailed sample is available @ https://github.com/sathishk/zols-sample.

Anyways, For testing purpose, you can run zols as give below.

Prerequisites
-------------
JDK 1.7

Maven

MongoDB

Git

Steps:
---------------
git clone https://github.com/sathishk/zols.git

cd zols

mvn clean install

cd webApp

mvn tomcat7:run

That is all. You can access the application @ http://localhost:8080/zols


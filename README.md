Zols
====

Zols is a Light weighted Content Management System built on Spring Framework. Apart from providing everything you want from a CMS, Zols is unique in three ways:

### Embeddable CMS
Zols can either be run as a standalone CMS or as an embedded CMS within an existing Spring Web Application. This gives the power of quickly bootstrapping and testing our your HTML authoring and be able to quickly deploy it in the production. 

All you need is to include some jars to your Spring MVC application. Your CMS is in your application straight away

### From HTML to CMS in no time
Directly link your HTML Projects to CMS. Make changes to HTML files and see it up and running in no time.This gives the power of quickly bootstrapping and testing our your HTML authoring and be able to quickly deploy it in the production.

### Native Data Format
Content is no longer just a text. Any data we enter here is stored in it's native format. For E.g Dates, Time, Number etc. Which we can read, update, delete and query by clearly documented REST API

Installation:
==========

As mentioned above, you don't need to run zols as web application. You can include zols into your Spring application easily. Detailed sample is available [here][].

For testing purpose, you can run zols as given below in the steps section.

### Prerequisites
JDK 1.7

Maven

MongoDB

Git

### Steps:
1) Start a mongo server as documented in the [mongo website][]. 

2) `git clone https://github.com/sathishk/zols.git`

3) `cd zols`

4) `mvn clean install`

5) `cd webApp`

6) `mvn tomcat7:run`

You can access the application @ `http://localhost:8080/zols`

Login to the application using `username/password` as `admin/admin`.

## License
Zols is released under version 2.0 of the [Apache License][].

[here]:https://github.com/sathishk/zols-sample
[mongo website]: https://www.mongodb.org/
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

GWT+GAE sample app using Maven, [RequestFactory](http://code.google.com/webtoolkit/doc/latest/DevGuideRequestFactory.html), and [Objectify](http://code.google.com/p/objectify-appengine/)

### Quick start ###
```
svn checkout https://listwidget.googlecode.com/svn/trunk/ listwidget
cd listwidget/web
mvn gwt:run
```

To import the project into Eclipse 3.5 or 3.6, do the following in Eclipse:

  1. [Install Google Plugin for Eclipse](http://code.google.com/eclipse/docs/getting_started.html)
  1. Install m2eclipse plugin from http://m2eclipse.sonatype.org/sites/m2e
  1. Install m2e-extras plugin from http://m2eclipse.sonatype.org/sites/m2e-extras
  1. File | Import | Existing Maven project and browse to the listwidget/web directory

You can then Run As | Web Application.

Related blog post: [Using GWT RequestFactory with Objectify](http://turbomanage.wordpress.com/2011/03/25/using-gwt-requestfactory-with-objectify/)
![GitHub](https://img.shields.io/github/license/studio42gmbh/dl)
![GitHub top language](https://img.shields.io/github/languages/top/studio42gmbh/dl)
![GitHub last commit](https://img.shields.io/github/last-commit/studio42gmbh/dl)
![GitHub issues](https://img.shields.io/github/issues/studio42gmbh/dl)
<!-- ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/studio42gmbh/dl/Java%20CI%20with%20Maven) -->

# Data Language (ALPHA)

![DL Logo](https://github.com/studio42gmbh/dl/blob/master/resources/images/logo/dl-logo-200.png)

***ATTENTION:** This is the ALPHA release. Still many changes to come!*

After many years working with data driven projects i came to love and hate XML and JSON for its great possibilities, 
but for the lack of good ways to make sure the data is valid and consistent.
Oh and comments are part of the language!

With Data Language (DL) i want to close that gap.

Have a great day!

Benjamin

> "Look up to the stars not down on your feet. Be curious!" _Stephen Hawking 1942 - 2018_


## Get started

* Download project
* Download Base 42 https://github.com/studio42gmbh/base42
* Download Log 42 https://github.com/studio42gmbh/log42
* Use in your projects

For details to the language check out the [Wiki](https://github.com/studio42gmbh/dl/wiki)

Find the [Javadoc here](https://studio42gmbh.github.io/dl/javadoc/)


## Simple Example

The following example gives a short glimpse into the possibilities of DL.

For more examples see the [Data Language Examples](https://github.com/studio42gmbh/dle)

Especially the example [DL Only Example](https://github.com/studio42gmbh/dle#dl-only-example) showcases the possibilites of DL.

```

// simple enum defined inside DL
enum MemberLevel 
{
  Guest, Member, Gold, Platin
}

extern annotation de.myapp.CheckConsistencyAnnotation;
alias checkConsistency de.myapp.CheckConsistencyAnnotation;

extern type de.myapp.Entity;
alias Entity de.myapp.Entity;

/**
 * Represents a user in the app.
 */
type User @checkConsistency extends Entity
{
  String login @required @length(4, 120);
  UUID id @required;
  MemberLevel level @required : Guest;
}

/**
 * Represents the app config.
 */
type Config @checkConsistency extends Entity contains User
{
  String title @required @length(5, 50) : "My App";
  int seed @required @range(1, 1000000000);
  boolean debug : false;
  UUID id @required;
}

// from here on no types or enums can be defined
pragma disableDefineTypes;

// Defines a named instance configuration of type Config
Config configuration @export {
  seed : 1536;
  id : "4a38dc5c-13b4-4817-ac24-b3e15616f884";
  
  User {
    login : "Arthur";
    level : Gold;
    id : "2310889e-2bc9-4641-b930-dcc59c3818a4";
  }
}

```


## Features

* Define data JSON-like
* Define types, enums and annotations for describing and validating the structure of your data
* Standardized streamable binary format
* Java integration
  * Rich set of existing types, annotations and pragmas
  * Extend types, annotations, pragmas
  * Define types directly from java classes with DLCore.defineTypeFromClass(...)


## Future Plans

* Add integration for C++
* Add integration for C#
* Add integration for Python
* Add integration for JS
* Improve performance of readers and writers
* Extend the unit tests to cover even more examples and constructs


## The Language

For more information check out the [Wiki](https://github.com/studio42gmbh/dl/wiki)

* [Types](https://github.com/studio42gmbh/dl/wiki/Types)
* [Instances](https://github.com/studio42gmbh/dl/wiki/Instances)
* [Enums](https://github.com/studio42gmbh/dl/wiki/Enums)
* [Annotations](https://github.com/studio42gmbh/dl/wiki/Annotations)
* [Aliases](https://github.com/studio42gmbh/dl/wiki/Aliases)
* [Requires](https://github.com/studio42gmbh/dl/wiki/Requires)
* [Pragmas](https://github.com/studio42gmbh/dl/wiki/Pragmas)

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

The Data Language is designed to allow complex data driven projects to be more successful.
Its simple syntax is close to JSON. But DL also provides extensible language constructs for types, enums, annotations and pragmas.
It also allows you to modularize.
More explanations will come in future.

Data Language provides a new state "loadTime" in your application life cycle (develop, compile, deploy, run). 
This allows you to easily make sure that just validated and consistent data enters your application.
The possibilities range much further than any JSON schema can provide.

Have a great day!

Benjamin

> "Look up to the stars not down on your feet. Be curious!" _Stephen Hawking 1942 - 2018_


## Simple Example

The following example gives a short glimpse into the possibilities of DL.

```

// simple enum defined inside DL
enum MemberLevel 
{
  Guest,
  Member,
  Gold,
  Platin
}

/**
 * Represents a user in the app.
 */
type de.myapp.User @java
{
  String login @required @length(4, 120);
  UUID id @required;
  MemberLevel level @required : Guest;
}
alias User de.myapp.User;

/**
 * Represents the app config.
 */
type de.myapp.Config @java
{
  String title @required @length(5, 50) : "My App";
  int seed @required @range(1, 1000000000);
  boolean debug : false;
  UUID id @required;
  User user @required;
}
alias Config de.myapp.Config;

// from here on no types or enums can be defined
pragma disableDefineTypes;

// Defines a named instance configuration of type Config
Config configuration {
  seed : 1536;
  id : "4a38dc5c-13b4-4817-ac24-b3e15616f884";
  user : User {
    login : "Arthur";
    level : Gold;
    id : "2310889e-2bc9-4641-b930-dcc59c3818a4";
  };
}

```


## Get started

* Download project
* Download Base 42 https://github.com/studio42gmbh/base42
* Download Log 42 https://github.com/studio42gmbh/log42
* Use in your projects

Find the Javadoc here: https://studio42gmbh.github.io/dl/javadoc/


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

All entities types, annotations, enums, pragmas and instances are extensible.

You can find the complete ANTLR grammar here: 
https://github.com/studio42gmbh/dl/tree/master/src/main/antlr4/de/s42/dl/parser


### Types

Types allow to define the allowed entities in your domain dialect. They provide a flexible hierarchical system.
Type names have to be unique. Simple types represent a data type like String, UUID, .... Complex types represent class/struct-like data.
Types can easily be extended (See https://github.com/studio42gmbh/dl/tree/master/src/main/java/de/s42/dl/types)

```
EXTERN? ( FINAL | ABSTRACT )? TYPE name @annotation* 
( EXTENDS parent (, parent )* )? 
( CONTAINS contained (, contained )* )?
{
  type ( < type (, type)* > )? name @annotation* ( : ("default" | instance | $reference ))? ;

  REQUIRE moduleId;
}
```
                

### Instances

Instances are your data. Each instance has a type and can then define its attribute values. Also assignments of references are allowed.
Names of named instances have to be unique in the same container.

```
type ( name )? @annotation* {

  type? name : ( value | instance | $reference ) ;

  instance

  REQUIRE moduleId;
}
```
                

### Annotations

Annotations allow you to give qualities, contracts, ... to your types, attributes and instances.
Annotations can easily be extended (See https://github.com/studio42gmbh/dl/tree/master/src/main/java/de/s42/dl/annotations)

```
EXTERN? ANNOTATION name @annotation*;
```
                

### Pragmas

Pragmas allow you to set system properties like i.e. if you are allowed to define further types etc.
Pragmas can easily be extended (See https://github.com/studio42gmbh/dl/tree/master/src/main/java/de/s42/dl/pragmas)

```
PRAGMA name ( (parameter (, parameter )* ) )?;
```
                

### Alias

Alias allows to define another name for your types.

```
ALIAS alias typeName;
```
                

### Require

Require other modules to be loaded. The resolvers can be extended. 
By default they provide a file and a resource resolvment in the java implementation.
Resolvers can easily be extended (See https://github.com/studio42gmbh/dl/tree/master/src/main/java/de/s42/dl/core/resolvers)

```
REQUIRE moduleId;
```
                

### Enums

Enums allow to define enumerations. In the java implementation native enumerations can easily be loaded as types with DLCore.createEnum(...)

```
EXTERN? ENUM name @annotation*
{
  value (, value)*
}
```

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

The [full example](https://github.com/studio42gmbh/dle#simple-configuration-example) is contained in the DL Examples.
For more examples see the [Data Language Examples](https://github.com/studio42gmbh/dle)

Especially the example [DL Only Example](https://github.com/studio42gmbh/dle#dl-only-example) showcases the possibilites of DL.

### File in DL

In its simple uage DL resembles JS like data structures.

```js
/*
 * A simple configuration mapped onto a java class in app
 */

Configuration config @export {

	login	: "Arthur Smith"; 
	id		: 1456745655;
	depth	: 1.5629345793985692;
	active	: true;
	uuid	: "ad232384-4a04-4119-9e95-4753f31e3b09";
	tags	: modern, simple, flexible;
	scores	: 1.453, 2.534, 9.232, 4.553;
	mapped	: a, 1.2, b, 2.7, c, 3.3; // always key, value, ...
}
```

### Java code to load DL

With a few lines of Java code you can load the DL already converted into your POJO Configuration class:

```java
package de.s42.dl.examples.simpleconfiguration;

import de.s42.dl.DLType;
import de.s42.dl.core.DefaultCore;
import de.s42.dl.util.DLHelper;
import java.nio.file.Path;

public class Main
{
	
	protected final static Path CONFIG_PATH = Path.of("de/s42/dl/examples/simpleconfiguration/config.dl");
	
	public static void main(String[] args) throws Exception
	{
		// Setup dl core and map own POJO class Configuration
		DefaultCore core = new DefaultCore();
		DLType type = core.defineType(Configuration.class, "Configuration");

		// Load config -> as it is a dl file with just a single entity we can use this helper method
		Configuration config = DLHelper.readInstanceFromFile(core, CONFIG_PATH);
	}
}
```

This is the Data Class Configuration:

```java
package de.s42.dl.examples.simpleconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Configuration
{

	protected String login;
	protected UUID uuid;
	protected int id;
	protected double depth;
	protected boolean active;
	protected String[] tags;
	protected final List<Double> scores = new ArrayList<>();
	protected Map<String, Double> mapped = new HashMap<>();
  
  // ... getters and setters
}
```

Here are some screenshots of projects we have developed internally to test DL. We built a whole data driven opengl test engine on DL already and tried 2 test projects with it.

![Screenshot GP](https://github.com/studio42gmbh/dl/blob/master/resources/images/screenshots/2022-04-24-screener-dl-alpha-based-gp.jpg)
![Screenshot SW](https://github.com/studio42gmbh/dl/blob/master/resources/images/screenshots/2022-04-24-screener-dl-alpha-based-sw.jpg)



## Features

* Define data JSON-like
* Define types, enums and annotations for describing and validating the structure of your data
* Standardized streamable binary format
* Java integration
  * Rich set of existing types, annotations and pragmas
  * Extend types, annotations, pragmas
  * Define types directly from java classes with DLCore.defineTypeFromClass(...)


## Roadmap / Future Plans

The current ALPHA state means that there will still be regular and potentially breaking changes in DL. During the ALPHA phase Java will remain the supported integration language.

The BETA phase will provide a stable DL. The focus will be on stability and performance. In this phase new language integrations will be added.

This are the overall next bigger features:

* Finish DL specification
  * Explore expressions in Assignments
  * Explore combined annotations (for contracts etc.)
* Improve performance of readers and writers
* Extend the unit tests to cover even more examples and constructs
* Add integration for C++
* Add integration for C#
* Add integration for Python
* Add integration for JS


## The Language

For more information check out the [Wiki](https://github.com/studio42gmbh/dl/wiki)

* [Types](https://github.com/studio42gmbh/dl/wiki/Types)
* [Instances](https://github.com/studio42gmbh/dl/wiki/Instances)
* [Enums](https://github.com/studio42gmbh/dl/wiki/Enums)
* [Annotations](https://github.com/studio42gmbh/dl/wiki/Annotations)
* [Aliases](https://github.com/studio42gmbh/dl/wiki/Aliases)
* [Requires](https://github.com/studio42gmbh/dl/wiki/Requires)
* [Pragmas](https://github.com/studio42gmbh/dl/wiki/Pragmas)

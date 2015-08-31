---
title: "Monger, a Clojure MongoDB client: miscellaneous | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers minor topics that do not fit into any other guide.

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What Version of Monger Does This Guide Cover?

This guide covers Monger 3.0 (including preview releases).


## What Version of MongoDB Does This Guide Cover?

Content in this guide assumes MongoDB 3.0 or later but may be
relevant for earlier releases as well.


## Logging Configuration

Modern MongoDB Java driver version [use SLF4J](https://groups.google.com/d/msg/mongodb-user/_t5rHlaxYxI/aNdlMVIrcR0J)
for logging. If Java driver logging is undesired, the easiest way to turn
it off is to add a dependency on `slf4j-nop`: 

    [org.slf4j/slf4j-nop "1.7.12"]


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.

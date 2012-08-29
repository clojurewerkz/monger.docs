---
title: "Monger, a Clojure MongoDB client: Using MongoDB commands | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Using MongoDB commands
 * Using auxilliary functions that help with database administration


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.2.


## Overview

The MongoDB command interface provides access to all non CRUD database operations. Fetching server stats, initializing a replica set, and running a map-reduce job are all
accomplished by running a command.

You specify a command first by constructing a standard BSON document whose first key is the name of the command. For example, specify the isMaster command using the following
BSON document (demonstrated here as a Clojure map):

{% gist ada23f7d614b71897b41 %}

Some MongoDB commands are version-specific. Some implement sophisticated features, for example, the Aggregation Framework support, and some are administrative
in nature, like reindexing a collection.

With Monger, you use `monger.core/command` function to run commands. `monger.result/ok?` can be used to determine if a command succeeded or not, `monger.conversion/from-db-object`
is used to convert a command results to a Clojure map. Here is an example that demonstrates all three:

{% gist 4ed73db500d479ea83a3 %}

[MongoDB command reference](http://docs.mongodb.org/manual/reference/commands/?highlight=commands) lists available DB commands. Command document structure with Monger
is exactly the same as in the MongoDB shell and MongoDB manual guides. If your command happens to include operators (`$gt`, `$lt`, `$regex`, etc), you can
use macros in the `monger.operators` namespace, just like with queries.

Monger command API was designed to for flexibility: it lets developers to use new commands as soon as MongoDB server supports them. However,
many commands are used more often than others and their API does not change any more. Monger provides convenient functions for some of them
in the `monger.command` namespace.


## Common Commands

### Get collection stats

`monger.command/collection-stats` takes a collection names and returns collection stats:

{% gist 7f6932dd334fd2601ba4 %}


### Get database stats

`monger.command/db-stats` is similar to `monger.command/collection-stats` but returns database stats:

{% gist fc94dc0f086f58daa0e6 %}


### Reindex a collection

{% gist 20ec74181ee5dc0cd017 %}


### Get server status

`monger.command/server-status` returns stats for the MongoDB server Monger is connected to.


### Run "top" command

`monger.command/top` provides programmatic access to the same information `mongotop` command line tool outputs.

Specifically, it returns raw usage of each database, and provides amount of time, in microseconds, used and a count of operations for the following event types:

* total
* readLock
* writeLock
* queries
* getmore
* insert
* update
* remove
* commands


## What to Read Next

Congratulations, this is the last guide. For the definitive list of commands MongoDB supports, see [MongoDB command reference](http://docs.mongodb.org/manual/reference/commands/?highlight=commands).

Take a look at [other guides](/articles/guides.html), they cover all kinds of topics.


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.

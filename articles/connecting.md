---
title: "Monger, a Clojure MongoDB client: Connecting to MongoDB"
layout: article
---

## About this guide

This guide covers:

 * Connecting to MongoDB
 * Tuning database connection (concurrency level, automatic reconnection, timeouts, etc)
 * Connecting to MongoDB using connection URI
 * Connecting to replica sets
 * Connecting in PaaS environments, for example, MongoHQ add-on on Heroku
 * Choosing default database
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0-beta7, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.


## Overview

Monger supports working with multiple connections and/or databases but is optimized for applications that only use one connection
and one database.

To connect, you use `monger.core/connect!` and `monger.core/connect` functions. A basic example:

{% gist 2af1bcee22e0f14a8741 %}


## Connecting to MongoDB using connection options

Passing no arguments to `monger.core/connect` is very common for development but production environments typically require
connection options tweaks.

This is done by using 2-arity form of `monger.core/connect!` that takes a server address and connection options. Because MongoDB connections have
a good dozen of tunable parameters, Monger provides a function that takes a map of them and produces an object that
can be used as connection options:

{% gist c7ff27a49252772a92dd %}

### Supported connection options

#### :connections-per-host (default: 10)

The maximum number of connections allowed per host for this Mongo instance.
Those connections will be kept in a pool when idle.
Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection.


#### :threads-allowed-to-block-for-connection-multiplier (default: 5)

This multiplier, multiplied with the connectionsPerHost setting, gives the maximum number of threads that
may be waiting for a connection to become available from the pool. All further threads will get an exception right away.
For example if connectionsPerHost is 10 and threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50 threads can wait for a connection.


#### :max-wait-time (default: 120,000)

The maximum wait time in milliseconds that a thread may wait for a connection to become available.
A value of 0 means that it will not wait. A negative value means to wait indefinitely.


#### :connect-timeout (default: 0)

The connection timeout in milliseconds. Only used for new connections. A value of 0 means no timeout.


#### :socket-timeout (default: 0)

The socket timeout in milliseconds. A value of 0 means no timeout.


#### :socket-keep-alive (default: false)

This flag controls the socket keep alive feature that keeps a connection alive through firewalls.


#### auto-connect-retry (default: true)

If true, the client will keep trying to connect to the same server in case that the socket cannot be established.


#### max-auto-connect-retry-time (default: 15 seconds)

he maximum amount of time in milliseconds to spend retrying to open connection to the same server.


#### w

`:w` value of the default write concern of the connection. With Monger, setting default write concern using
`monger.core/set-default-write-concern!` largely eliminates the need for this option.


#### w-timeout

`:w-timeout` value of the default write concern of the connection. With Monger, setting default write concern using
`monger.core/set-default-write-concern!` largely eliminates the need for this option.


#### fsync

`:fsync` value of the default write concern of the connection: should all operations wait for server to sync changes to disk?
With Monger, setting default write concern using `monger.core/set-default-write-concern!` largely eliminates the need for this option.


## Connecting via URI (e.g. MongoHQ or MongoLab on Heroku)

In PaaS environments like Heroku, it is very common that the only way to connect to external databases is via connection URL.
With Monger you can do that using the `monger.core/connect-via-uri!` function. In the following example, connection URI
is taken from the `MONGODB_URI` env variable if it is set. If that's not the case, "mongodb://127.0.0.1/monger-test4" is
used as a fallback (for example, for development):

{% gist a2810dee0a221b95d899 %}



## Connecting to a replicate set

Monger supports connecting to replica sets using one or more seeds when calling `monger.core/connect` with a collection of server
addresses instead of just a single one:

{% gist f365286bf17cb5ebbc33 %}

`monger.core/connect!` function works exactly the same way.



## Choosing default database

Before your application begins to perform queries, updates and so on, it is necessary to tell Monger what database it should use by default.
To do it, use `monger.core/get-db` and `monger.core/set-db!` functions in combination:

{% gist 86340be2cabe43a4ec18 %}


## What to Read Next

The documentation is organized as a number of guides, covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Inserting documents](/articles/inserting.html)
 * [Querying & finders](/articles/querying.html)
 * [Updating documents](/articles/updating.html)
 * [Deleting documents](/articles/deleting.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.

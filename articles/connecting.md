---
title: "Monger, a Clojure MongoDB client: Connecting to MongoDB | MongoDB library for Clojure"
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

This guide covers Monger 1.7 (including beta releases).


## Overview

Monger supports working with multiple connections and/or databases but is optimized for applications that only use one connection
and one database.

To connect, you use `monger.core/connect!` and `monger.core/connect` functions. A basic example:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

;; localhost, default port
(mg/connect!)

;; given host, given port
(mg/connect! { :host "db.megacorp.internal" :port 7878 })


;; given host, given port
(mg/connect! { :host "db.megacorp.internal" :port 7878 })

;; using MongoOptions allows fine-tuning connection parameters,
;; like automatic reconnection (highly recommended for production environment)
(let [^MongoOptions opts (mg/mongo-options :threads-allowed-to-block-for-connection-multiplier 300)
      ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)]
  (mg/connect! sa opts))

```


## Connecting To Mongodb Using Connection Options

Passing no arguments to `monger.core/connect` is very common for
development but production environments typically require connection
options tweaks.

This is done by using 2-arity form of `monger.core/connect!` that
takes a server address and connection options. Because MongoDB
connections have a good dozen of tunable parameters, Monger provides a
function that takes a map of them and produces an object that can be
used as connection options:

``` clojure
(let [^MongoOptions opts (mongo-options :threads-allowed-to-block-for-connection-multiplier 300)
        ^ServerAddress sa (server-address "127.0.0.1" 27017)]
    (monger.core/connect! sa opts))
```

### Supported Connection Options

#### :connections-per-host (default: 10)

The maximum number of connections allowed per host for this Mongo instance.
Those connections will be kept in a pool when idle.
Once the pool is exhausted, any operation requiring a connection will block waiting for an available connection.


#### :threads-allowed-to-block-for-connection-multiplier (default: 5)

This multiplier, multiplied with the connectionsPerHost setting, gives
the maximum number of threads that may be waiting for a connection to
become available from the pool. All further threads will get an
exception right away.  For example if connectionsPerHost is 10 and
threadsAllowedToBlockForConnectionMultiplier is 5, then up to 50
threads can wait for a connection.


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

``` clojure
(ns my.service
  (:require [monger.core :as mg]))

(defn -main
  [& args]
  (let [uri (get (System/getenv) "MONGODB_URI" "mongodb://127.0.0.1/monger-test4")]
    (monger.core/connect-via-uri! uri)))
 ```



## Connecting To A Replica Set

Monger supports connecting to replica sets using one or more seeds when calling `monger.core/connect` with a collection of server
addresses instead of just a single one:

``` clojure
(ns my.service
  (:require monger.core :refer [connect connect! server-address mongo-options]))

;; Connect to a single MongoDB instance
(connect (server-address "127.0.0.1" 27017) (mongo-options))

;; Connect to a replica set
(connect [(server-address "127.0.0.1" 27017) (server-address "127.0.0.1" 27018)]
         (mongo-options))
```

`monger.core/connect!` function works exactly the same way.


## Authentication

With Monger, authentication is performed on database instance.
`monger.core/authenticate` is the function used for that. It takes a database instance, a username
and a password (as char array):

``` clojure
(require '[monger.core :as mc])

(let [username "a-user"
      password "14)'dbRYAzI(37liCfgc"
      db       "a-db"]
  (mc/connect!)
  (mc/use-db! db)
  (mc/authenticate (mc/get-db db) username (.toCharArray password)))
```

To connect to a replicate set that requires authentication with Monger, use example in the section above
to connect, then authenticate the same way.


## Choosing Default Database

Before your application begins to perform queries, updates and so on, it is necessary to tell Monger what database it should use by default.
To do it, use `monger.core/get-db` and `monger.core/set-db!` functions in combination:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg]))

;; localhost, default port
(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))
```


## What to Read Next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Inserting documents](/articles/inserting.html)
 * [Querying & finders](/articles/querying.html)
 * [Updating documents](/articles/updating.html)
 * [Deleting documents](/articles/deleting.html)
 * [Indexing and other collection operations](/articles/collections.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.

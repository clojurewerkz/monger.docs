---
title: "Monger, a Clojure MongoDB client: storing large files in MongoDB using GridFS | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Using GridFS with Monger


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 3.1 (including preview releases).


## Overview

GridFS is a feature for storing large files in MongoDB. It streams
files to the database in chunks so it is possible to store very large
files without having to load the entire file in RAM to store it.


## Monger 2.0+ Public API Convention

Monger versions prior to 2.0 used dynamic vars (shared state) to store
GridFS references. Monger 2.0 is different: it
accepts them as an explicit argument. Functions that store
or load files require a GridFS reference as their 1st argument
in 2.0.


## Storing files in GridFS

To store a file in MongoDB using Monger, you use a DSL from the `monger.gridfs` namespace:

``` clojure
(ns my.service
  (:require [monger.core :as mg]
            [monger.gridfs :refer [store-file make-input-file filename content-type metadata]]))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      fs   (mg/get-gridfs conn "monger-test")]
  ;; store a file from a local FS path with the given filename, content type and metadata
  (store-file (make-input-file fs "/path/to/a/local/file.png")
    (filename "image.png")
    (metadata {:format "png"})
    (content-type "image/png")))
```

`monger.gridfs/make-input-file` is a polymorphic function that accepts
`java.io.File` and `java.io.InputStream` instances as well as
byte arrays as its content argument.

When a file is stored, md5 chechsum will be calculated for its
contents automatically.


## Querying for files stored on GridFS

Files stored on GridFS are stored in two collections:

 * Chunks are stored with a reference back to the document that represents a file the chunk belongs to.
 * Files metadata (md5 chechsum, content type, filename, arbitrary user metadata) is stored as a separate document for each file.

Getting a file out of GridFS is thus a two step operation:

 * Query for a file
 * Store the file to the local filesystem (or stream it, etc). Chunks will be streamed back to the client that will store them.

It is common to query files by object id, md5 checksum or filename,
although you can query them just like you would any collection of
documents. To do so, use `monger.gridfs/find`,
`monger.gridfs/find-one`, `monger.gridfs/find-maps`,
`monger.gridfs/find-one-as-map` functions. If you need to store or
stream file, you'd need to use `monger.gridfs/find` or
`monger.gridfs/find-one` which will return back
`com.mongodb.gridfs.GridFSDBFile` instances. They can be stored to
disk to treated as input streams:

``` clojure
(ns my.service
  (:require [monger.core :as mg]
            [monger.gridfs :as gfs :refer [store-file make-input-file filename content-type metadata]]))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      fs   (mg/get-gridfs conn "monger-test")]
  (store-file (make-input-file fs "/path/to/a/local/file.png")
    (filename "image.png")
    (metadata {:format "png"})
    (content-type "image/png"))
  
  ;; returns a list of GridFSDBFile instances. Each of them can be stored to
  ;; disk using GridFSDBFile#writeTo method or converted to input stream with GridFSDBFile#getInputStream
  (gfs/find fs {:filename "image.png"})
  
  (-> (gfs/find-one fs {:filename "image.png"})
      (.writeTo "/a/new/location.png")))
```

If you just need to access file metadata, you can load it directly as
Clojure maps using `monger.gridfs/find-maps` and
`monger.gridfs/find-one-as-map`:

``` clojure
(ns my.service
  (:require [monger.core :as mg]
            [monger.gridfs :as gfs :refer [store-file make-input-file filename content-type metadata]]))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      fs   (mg/get-gridfs conn "monger-test")]
  (store-file (make-input-file fs "/path/to/a/local/file.png")
    (filename "image.png")
    (metadata {:format "png"})
    (content-type "image/png"))
  
  ;; returns a list of file metadata documents as Clojure maps
  (gfs/find-maps fs {:filename "image.png"})
  
  ;; same as (first (gridfs/find-maps …))
  (gfs/find-one-as-map fs {:filename "image.png"}))
```

If you want a list of *all* files, use `monger.gridfs/files-as-maps`
or `monger.gridfs/all-files` functions with a single argument: a GridFS
instance.


## Deleting Files Stored on GridFS

Deleting files on GridFS is very similar to deleting documents from a
collection. You use the `monger.gridfs/remove` function that takes a
query condition:

``` clojure
(ns my.service
  (:require [monger.core :as mg]
            [monger.gridfs :as gfs :refer [store-file make-input-file filename content-type metadata]]))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      fs   (mg/get-gridfs conn "monger-test")]
  (store-file (make-input-file fs "/path/to/a/local/file.png")
    (filename "image.png")
    (metadata {:format "png"})
    (content-type "image/png"))
  
  ;; deletes a file
  (gfs/remove fs {:filename "image.png"})
  
  ;; deletes a file by md5 checksum
  (gfs/remove fs {:md5 "1942f1e69c1d7354f93e2cf805894a9c"}))
```


## What to read next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)



## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.

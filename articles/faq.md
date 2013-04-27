---
title: "Monger, a Clojure MongoDB client: FAQ | MongoDB library for Clojure"
layout: article
---

## How is Monger Licensed?

Starting with version 1.6, Monger is double-licesned under the
[Eclipse Public License
1.0](http://www.tldrlegal.com/license/eclipse-public-license-1.0-(epl-1.0)
(the same as Clojure) and [Apache Public
License](http://www.tldrlegal.com/license/apache-license-2.0-(apache-2.0)).

Earlier versions are licensed under the [Eclipse Public License 1.0](http://www.tldrlegal.com/license/eclipse-public-license-1.0-(epl-1.0).


## What Clojure Versions Are Supported?

Monger is built from the ground up for Clojure 1.3 and later.


## What MongoDB Versions Are Supported?

Monger tries to use the most recent MongoDB Java driver available
under the hood and thus supports MongoDB 2.0 and later
versions. Please note that some features may be specific to MongoDB
2.2, 2.4 or other releases.



## Why Not Contribute to [insert an existing Clojure MongoDB driver name]?

There is one MongoDB client for Clojure that has been around since 2009. So, why create another one? Monger authors
wanted a client that will

 * Support most of MongoDB 2.0+ features but only those that really matter. Grouping the way it is done today, for example, does not (it is easier to just use Map/Reduce directly).
 * Be well documented.
 * Be well tested.
 * Be maintained, do not carry technical debt from 2009 forever.
 * Target Clojure 1.3.0 and later from the ground up.
 * Integrate with libraries like clojure.data.json, clojure.core.cache and clj-time (Joda Time).
 * Provide support for unit testing: factories/fixtures DSL, collection cleaner functions, clojure.test integration and so on.
 * Support URI connections to be friendly to Heroku and other PaaS providers.
 * Integrate usage of JavaScript files and ClojureScript (as soon as ClojureScript compiler is ready for embedding).

We could not see how we could do this by contributing to an existing library with certain technical debt accumulated over
2+ years and many existing applications depending on its APIs not changing in any significant way. In August 2011 when
Monger was started, it wasn't yet time to make CongoMongo move all the way to Clojure 1.3.0 (which was in alpha-beta release stage
at the time).



## Is it fast?

### Write Performance

Monger insert operations are efficient and have very little overhead compared to the underlying Java driver. Here
are some numbers on a MacBook Pro from fall 2010 with Core i7 and an Intel SSD drive:

<pre>
Testing monger.test.stress
Inserting  1000  documents...
"Elapsed time: 25.699 msecs"
Inserting  10000  documents...
"Elapsed time: 135.069 msecs"
Inserting  100000  documents...
"Elapsed time: 515.969 msecs"
</pre>

With the `SAFE` write concern, it takes roughly 0.5 second to insert 100,000 documents after JVM warm-up with Clojure 1.3.0.
While these are certainly not scientific numbers (and latency distribution is not provided), they can serve as a decent
ball part figure: Monger has extremely low overhead over the MongoDB Java driver and you can expect it to process hundreds to tens of
thousands of documents per second, depending on the workload.




## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.

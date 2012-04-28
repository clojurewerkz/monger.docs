---
title: "Monger, a Clojure MongoDB client: FAQ"
layout: article
---

## How is Monger licensed?

Monger is released under the [Eclipse Public License 1.0](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.


## What Clojure versions are supported?

Monger is built from the ground up for Clojure 1.3 and later.


## What MongoDB versions are supported?

Monger currently uses MongoDB Java driver 2.7.x under the hood and thus supports MongoDB 2.0 and later versions. Please note that some
features may be specific to MongoDB 2.2 and later versions.



## Why Not Contribute to CongoMongo?

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

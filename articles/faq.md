## How is Monger licensed?

Monger is released under the [Eclipse Public License 1.0](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.


## Is it fast?

### Write Performance

Monger insert operations are efficient and have very little overhead compared to the underlying Java driver. Here
are some numbers on a MacBook Pro from fall 2010 with Core i7 and an Intel SSD drive:

```
Testing monger.test.stress
Inserting  1000  documents...
"Elapsed time: 25.699 msecs"
Inserting  10000  documents...
"Elapsed time: 135.069 msecs"
Inserting  100000  documents...
"Elapsed time: 515.969 msecs"
```

With the `SAFE` write concern, it takes roughly 0.5 second to insert 100,000 documents with Clojure 1.3.0.

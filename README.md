# MongoDB Clojure Driver Documentation #

This is a documentation site for [monger](https://github.com/michaelklishin/monger), an idiomatic well mainteined Clojure MongoDB client.


## How to regenerate the site

In order to modify contents and launch dev environment, switch to Ruby 1.8.7 (JRuby and Rubinius in 1.8 mode also work fine) and run:

      cd _source
      bundle install
      bundle exec nanoc autocompile

In order to recompile assets for publishing, run

      ./_source/compile.sh

## Supported liquid helpers:

In order to add example to your docs, use example helper. Pass relative path to file (it should be located at ./source/content/examples folder) and Gist ID.

  {% example examples/01a_getting_started_snippet1.clj, 998727 %}


## License & Copyright

Copyright (C) 2011-2012 Alexander Petrov, Michael S. Klishin.

Distributed under the Eclipse Public License, the same as Clojure.

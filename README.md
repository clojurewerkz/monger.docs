# MongoDB Clojure Driver Documentation #

This is a documentation site for [monger](https://github.com/michaelklishin/monger), an idiomatic well mainteined Clojure MongoDB client.


## How to regenerate the site

In order to modify contents and launch dev environment, switch to Ruby 1.8.7 and run:

      cd _source
      bundle install
      bundle exec nanoc autocompile

In order to recompile assets for publishing, run

      ./_source/compile.sh

## Supported liquid helpers:

In order to add example to your docs, use example helper. Pass relative path to file (it should be located at ./source/content/examples folder) and Gist ID.

  {% example working_with_queues/01b_declaring_a_queue_using_queue_constructor.rb, 998727 %}

In order to add link to YARD docs (relevant for Ruby projects), use yard_link helper, and YARD notation for object/method, for example:

  {% yard_link AMQP::Queue#unbind %}

## License & Copyright

Copyright (C) 2011-2012 Alexander Petrov, Michael S. Klishin.

Distributed under the Eclipse Public License, the same as Clojure.

# mortgage-calc

## Developing

### Setup

The app will look for environment variables to configure the database connection (required) and HTTP port (defaults to 3000). I use direnv to set these when running the app locally:

```
# .envrc
export DATABASE_URL="jdbc:postgresql://localhost/mortgage?user=mortgage"
export PORT=3000
```

### Environment

> This is all part of the original README, but it still stands

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server. Changes to CSS or ClojureScript
files will be hot-loaded into the browser.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

If you want to access a ClojureScript REPL, make sure that the site is loaded
in a browser and run:

```clojure
dev=> (cljs-repl)
Waiting for browser connection... Connected.
To quit, type: :cljs/quit
nil
cljs.user=>
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

## Deploying

The app is running on Heroku.

If you have the Heroku toolbelt installed, you can simulate that by doing:

```sh
lein uberjar
export DATABSE_URL="SOME-SQL-DB"
heroku local
```

and browsing to `localhost://3000`.

## Legal

Copyright © 2018 David Duthie

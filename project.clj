(defproject mortgage-calc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies
  [
   [buddy/buddy-hashers "1.3.0"]
   [cljs-ajax "0.7.3"]
   [cljsjs/chartjs "2.7.0-0"]
   [duct/core "0.7.0"]
   [duct/module.ataraxy "0.3.0"]
   [duct/module.cljs "0.4.1"]
   [duct/module.logging "0.4.0"]
   [duct/module.sql "0.5.0"]
   [duct/module.web "0.7.0"]
   [duct/handler.sql "RELEASE"]
   [metosin/muuntaja "0.6.4"]
   [org.clojure/clojure "1.10.0"]
   [org.clojure/clojurescript "1.9.946"]
   [org.webjars/bootstrap "4.1.0"]
   [org.xerial/sqlite-jdbc "3.27.2"]
   [reagent "0.8.0"]
   [reagent-forms "0.5.40"]
   [ring-webjars "0.2.0"]
   ;; [secretary "1.2.3"]
   ;; [venantius/accountant "0.2.4"]
   [metosin/reitit "0.3.1"]
   ]
  :plugins [[duct/lein-duct "0.12.0"]]
  :main ^:skip-aot mortgage-calc.main
  :uberjar-name  "mortgage-calc-standalone.jar"
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev          [:project/dev :profiles/dev]
   :repl         {:prep-tasks   ^:replace ["javac" "compile"]
                  :dependencies [[cider/piggieback "0.4.0"]]
                  :repl-options {:init-ns          user
                                 :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
   :uberjar      {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.1"]
                                   [eftest "0.5.7"]
                                   [kerodon "0.9.0"]]}})

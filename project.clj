(defproject mortgage-calc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies
  [
   ;; Backend
   [buddy/buddy-hashers "1.3.0"]
   [duct/core "0.6.2"]
   [duct/handler.sql "0.3.1"]
   [duct/module.ataraxy "0.2.0"]
   [duct/module.cljs "0.3.2"]
   [duct/module.logging "0.3.1"]
   [duct/module.sql "0.4.2"]
   [duct/module.web "0.6.4"]
   [org.clojure/clojure "1.9.0"]
   [org.postgresql/postgresql "42.1.4"]

   ;; Frontend
   [cljs-ajax "0.7.3"]
   [cljsjs/chartjs "2.7.0-0"]
   [org.webjars/bootstrap "4.1.0"]
   [reagent "0.8.0"]
   [reagent-forms "0.5.40"]
   [ring-webjars "0.2.0"]
   [secretary "1.2.3"]
   [venantius/accountant "0.2.4"]
   ]
  :jvm-opts ["-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"]
  :plugins [[duct/lein-duct "0.10.6"]]
  :main ^:skip-aot mortgage-calc.main
  :uberjar-name  "mortgage-calc-standalone.jar"
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user
                         :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.2.0"]
                                   [eftest "0.4.1"]
                                   [kerodon "0.9.0"]]}})

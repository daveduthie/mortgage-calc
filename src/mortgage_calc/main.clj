(ns mortgage-calc.main
  (:gen-class)
  (:require [clojure.java.io :as io]
            [duct.core :as duct]
            [integrant.core :as ig]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys (or (duct/parse-keys args) [:duct/daemon])]
    (-> (duct/read-config (io/resource "mortgage_calc/config.edn"))
        (duct/prep keys)
        (duct/exec keys))))

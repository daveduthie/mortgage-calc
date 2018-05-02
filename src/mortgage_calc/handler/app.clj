(ns mortgage-calc.handler.app
  (:require
   [clojure.java.io :as io]
   [compojure.core :refer :all]
   [integrant.core :as ig]
   [ring.middleware.webjars :refer [wrap-webjars]]))

(def handler
  (GET "/" []
    ;; TODO: what's this?
    {:body {:example "data"}}
    (io/resource "mortgage_calc/handler/app/app.html")))

(def app
  (-> handler
      wrap-webjars))

(defmethod ig/init-key :mortgage-calc.handler/app [_ options]
  app)

(ns mortgage-calc.handler.app
  (:require
   [clojure.java.io :as io]
   [compojure.core :refer (GET)]
   [integrant.core :as ig]
   [ring.middleware.webjars :refer [wrap-webjars]]))

(def app (slurp (io/resource "mortgage_calc/handler/app/app.html")))

(def handler
  (-> (GET "/" [] app)
      wrap-webjars))

(defmethod ig/init-key :mortgage-calc.handler/app [_ options]
  handler)


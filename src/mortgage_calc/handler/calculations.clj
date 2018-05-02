(ns mortgage-calc.handler.calculations
  (:require
   [ataraxy.response :as response]
   [clojure.java.jdbc :as jdbc]
   [duct.database.sql]
   [duct.logger :refer [log]]
   [integrant.core :as ig]))

(defprotocol Calculations
  (create-calculation [db name price deposit term interest]))

(extend-protocol Calculations
  duct.database.sql.Boundary
  (create-calculation [{db :spec} name price deposit term interest]
    (let [results (jdbc/insert! db :calculations
                                {:name     name
                                 :price    price
                                 :deposit  deposit
                                 :term     term
                                 :interest interest})]
      (-> results ffirst val))))

(defmethod ig/init-key ::create [_ {:keys [db logger]}]
  (fn [{[_ name price deposit term interest] :ataraxy/result}]
    (let [id (create-calculation db name price deposit term interest)]
      (log logger :report ::create {:name name :id id})
      [::response/created (str "/calculations/" id)])))

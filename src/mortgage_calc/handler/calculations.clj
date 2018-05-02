(ns mortgage-calc.handler.calculations
  (:require
   [ataraxy.response :as response]
   [clojure.java.jdbc :as jdbc]
   [duct.database.sql]
   [duct.logger :refer [log]]
   [integrant.core :as ig]))

(defprotocol Calculations
  (create-calculation [db fields]))

(extend-protocol Calculations
  duct.database.sql.Boundary
  ;; TODO: validation
  (create-calculation [{db :spec} fields]
    (let [results (jdbc/insert! db :calculations fields)]
      (-> results ffirst val))))

(defmethod ig/init-key ::create [_ {:keys [db logger]}]
  (fn [{[_ fields] :ataraxy/result}]
    (log logger :report ::create {:fields fields})
    (let [id (->> [:name :price :deposit :years :interest :repayment]
                  (select-keys fields)
                  (create-calculation db))]
      [::response/created (str "/calculations/" id)])))

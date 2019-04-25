(ns mortgage-calc.handler.users
  (:require
   [ataraxy.response :as response]
   [buddy.hashers :as hashers]
   [clojure.java.jdbc :as jdbc]
   [duct.database.sql]
   [integrant.core :as ig])
  (:import (duct.database.sql Boundary)))

(defprotocol Users
  (create-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec} email password]
    (let [pw-hash (hashers/derive password)
          results (jdbc/insert! db :users {:email email, :password pw-hash})]
      (-> results ffirst val))))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password] :ataraxy/result}]
    (let [id (create-user db email password)]
      [::response/created (str "/users/" id)])))

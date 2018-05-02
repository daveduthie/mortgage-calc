(ns mortgage-calc.components.table
  (:require
   [mortgage-calc.util :as util]))

(defn table-view
  [splits]
  [:div.row
   [:div.col-md-2]
   [:div.col-md-8
    [:table.table
     [:thead {}
      [:tr {}
       [:th {} "Year"]
       [:th {} "Principal"]
       [:th {} "Principal repaid"]
       [:th {} "Interest serviced"]]]
     (into
      [:tbody]
      (map-indexed
       (fn [year {:keys [principal principal-repaid interest-serviced] :as datum}]
         ^{:key year}
         [:tr {}
          [:td {} year]
          [:td {} (util/format-rands principal)]
          [:td {} (util/format-rands principal-repaid)]
          [:td {} (util/format-rands interest-serviced)]])
       splits))]]])



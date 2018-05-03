(ns mortgage-calc.components.saved
  (:require
   [ajax.core :refer [GET]]
   [mortgage-calc.components.graph :as graph]
   [mortgage-calc.components.input :as input]
   [mortgage-calc.components.table :as table]
   [mortgage-calc.events :as ev]
   [mortgage-calc.util :as util]
   [reagent.core :as r]))


(def kw-prefix :mortgage-calc.components.saved)

(ev/register-simple-event-handler ::saved-calcs)

(defn list-calculations
  []
  ;; TODO: why does :transit response format not
  ;; decode values of :interest key properly
  (GET "/calculations"
       {:response-format :transit
        :handler
        (fn [results]
          (ev/emit ::saved-calcs results))
        :error-handler   util/log-error}))

(ev/register-simple-event-handler ::view)

(defn view-saved-btns
  [calc]
  [:div
   [:p [:button.btn.btn-link
         {:on-click #(ev/emit ::view [:table calc])} "view table"]]
   [:p [:button.btn.btn-link
         {:on-click #(ev/emit ::view [:graph calc])} "view graph"]]])

(defn view-saved
  []
  (let [doc (r/cursor ev/app-state [kw-prefix])
        _     (list-calculations)]
    (fn []
      (prn :ball-rolling)
      [:div.row
       [:div.col-md-3
        (into
         [:ul.list-group]
         (for [c (:saved-calcs @doc)]
           ^{:key c} [:li.list-group-item
                      (str (:name c) " (" (:id c) ")")
                      [view-saved-btns c]]))]
       [:div.col-md-7
        (when-let [[type calc] (:view @doc)]
          (let [splits (util/annual-splits calc)]
            (prn :t type :c calc)
            (case type
              :table [table/table-view splits]
              :graph [graph/graph-view splits]
              [:div])))]])))

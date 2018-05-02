(ns mortgage-calc.components.saved
  (:require
   [ajax.core :refer [GET]]
   [mortgage-calc.components.input :as input]
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

(defn view-saved
  []
  (let [calcs (r/cursor ev/app-state [kw-prefix])
        _     (list-calculations)]
    (fn []
      (prn :ball-rolling)
      [:div
       (into
        [:ul]
        (for [c (:saved-calcs @calcs)]
          ^{:key c} [:li (str c)]))])))

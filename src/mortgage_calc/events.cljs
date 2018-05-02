(ns mortgage-calc.events
  (:require
   [mortgage-calc.util :as util]
   [reagent.core :as r]))


;; State and events ---------------------------------------------------

(defonce app-state (r/atom {}))

(def event-handlers
  (atom {}))

(defn vectorise
  [path]
  (if (vector? path)
    path
    (let [prefix (-> path namespace keyword)
          id     (-> path name keyword)]
      [prefix id])))

(defn register-event-handler
  "Event handler should be a function: old-state, value -> new-state.
  Path may be either a vector or a namespaced keyword"
  [path handler]
  (let [p (vectorise path)]
    ;; (assert (not (get-in @event-handlers p)))
    (swap! event-handlers assoc-in p handler)))

(defn register-simple-event-handler
  [path]
  (let [p (vectorise path)]
    (register-event-handler p #(assoc-in %1 p %2))))

(defn event-handler [state path value]
  (if-let [handler (get-in @event-handlers path)]
    (handler state value)
    (do (util/log-error
         (str "No handler registered for " path))
        state)))

(defn emit [path val]
  ;; (println path val)
  (r/rswap! app-state event-handler (vectorise path) val)
  ;; (println :new-state @app-state)
  )

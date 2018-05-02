(ns mortgage-calc.client
  (:require
   [accountant.core :as accountant]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [mortgage-calc.components.input :as input]
   [mortgage-calc.components.saved :as saved]
   [reagent.core :as r]
   [secretary.core :as secretary])
  (:import
   goog.History
   goog.history.EventType))

(enable-console-print!)

;; State and events ---------------------------------------------------


(defonce app-state (r/atom {}))

(defn event-handler [state event-name value]
  (assoc state event-name value))

(defn emit [event-name val]
  (println event-name)
  (r/rswap! app-state event-handler event-name val))

;; Routes and history -------------------------------------------------

(defn nav-link [uri title page collapsed?]
  [:button.btn.button-default.navbar-button
   {:type :button}
   [:a {:href uri} title]])

(defn navbar []
  (let [collapsed? (r/cursor app-state [::collapsed])]
    (fn []
      [:nav.navbar.navbar-light.bg-primary
       [:a.navbar-brand.text-dark {:href "/"} "Mortgage Calculator"]
       [nav-link "/view-saved" "View saved calculations" :stock collapsed?]])))

;; Routes and history -------------------------------------------------

(secretary/defroute "/" []
  (emit ::current-page :input-page))

(secretary/defroute "/view-saved" []
  (emit ::current-page :view-saved))

(def pages
  {:input-page [input/input-page app-state emit]
   :view-saved [saved/view-saved app-state emit]})

;; Bootstrap ----------------------------------------------------------

(defn app
  [app-state]
  [:div
   [navbar]
   [:div.container
    [pages (::current-page @app-state)]]])

(defn ^:export init
  []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)

  (r/render [#'app app-state]
            (js/document.getElementById "app")))

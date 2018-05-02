(ns mortgage-calc.client
  (:require
   [accountant.core :as accountant]
   [mortgage-calc.components.input :as input]
   [mortgage-calc.components.saved :as saved]
   [mortgage-calc.events :as ev]
   [reagent.core :as r]
   [secretary.core :as secretary]))

(enable-console-print!)

;; Routes and history -------------------------------------------------

(defn nav-link [uri title page collapsed?]
  [:button.btn.button-default.navbar-button
   {:type :button}
   [:a {:href uri} title]])

(defn navbar []
  (let [collapsed? (r/cursor ev/app-state [::collapsed])]
    (fn []
      [:nav.navbar.navbar-light.bg-primary
       [:a.navbar-brand.text-dark {:href "/"} "Mortgage Calculator"]
       [nav-link "/view-saved" "View saved calculations" :stock collapsed?]])))

;; Routes and history -------------------------------------------------

(ev/register-event-handler
 ::current-page
 #(assoc %1 ::current-page %2))

(secretary/defroute "/" []
  (ev/emit ::current-page :input-page))

(secretary/defroute "/view-saved" []
  (ev/emit ::current-page :view-saved))

(def pages
  {:input-page #'input/input-page
   :view-saved #'saved/view-saved})

;; Bootstrap ----------------------------------------------------------

(defn app
  []
  [:div
   [navbar]
   [:div.container
    [(pages (::current-page @ev/app-state))]]])

(defn ^:export init
  []
  (prn :reloading)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (r/render [#'app] (js/document.getElementById "app")))

(init)

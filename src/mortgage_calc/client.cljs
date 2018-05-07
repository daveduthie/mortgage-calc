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

(defn nav-link [uri title]
  [:a.text-light
   {:href uri}
   title])

(defn navbar []
  [:nav.navbar.bg-dark
   [:a.navbar-brand.text-light {:href "/"} "Mortgage Calculator"]
   [nav-link "/view-saved" "View saved calculations"]])

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
  (when-let [root (js/document.getElementById "app")]
    (r/render [#'app] root)))

(init)

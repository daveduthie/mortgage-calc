(ns mortgage-calc.client
  (:require
   [mortgage-calc.components.input :as input]
   [mortgage-calc.components.saved :as saved]
   [mortgage-calc.events :as ev]
   [reagent.core :as r]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as reitit.frontend])
  (:import goog.History))

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

(def router
  (reitit/router
   [["/" :input-page]
    ["/view-saved" :view-saved]]))

(def pages
  {:input-page #'input/input-page
   :view-saved #'saved/view-saved})

(ev/register-event-handler ::current-page #(assoc %1 ::current-page %2))

(defn hook-browser-navigation! []
  (reitit.frontend/start!
   router
   (fn [match _]
     (ev/emit ::current-page (:name (:data match))))
   {:use-fragment false}))

;; Bootstrap ----------------------------------------------------------

(defn app
  []
  [:div
   [navbar]
   [:div.container
    (when-let [page (pages (::current-page @ev/app-state))]
      [page])]])

(defn ^:export init
  []
  (prn :reloading)
  (hook-browser-navigation!)
  (r/render [#'app] (js/document.getElementById "app")))

(init)

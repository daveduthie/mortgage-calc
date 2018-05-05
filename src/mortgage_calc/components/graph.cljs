(ns mortgage-calc.components.graph
  (:require
   [cljsjs.chartjs]
   [mortgage-calc.util :as util]
   [reagent.core :as r]
   [goog.object :as obj]
   ))

(def config
  (clj->js
   {:type    "line"
    :data    {:labels   []
              :datasets [{:label           "Principal repaid"
                          :data            []
                          :backgroundColor "rgba(0, 245, 100, 0.6)"
                          :borderWidth     1}
                         {:label           "Interest serviced"
                          :data            []
                          :backgroundColor "rgba(255, 0, 0, 0.4)"
                          :borderWidth     1}]}
    :options {:scales    {:yAxes [{:stacked true}]}
              :animation {:duration 250
                          :easing   "easeOutQuint"}}}))

;; FIXME: this fiddling around inside a js object fails under advanced compilation
(defn set-data!
  [graph splits]
  (let [labels         (take (count splits) (iterate inc 2019))
        data           (obj/get graph "data")
        principal-data (obj/getValueByKeys  data "datasets" 0)
        interest-data  (obj/getValueByKeys  data "datasets" 1)]
    (obj/set data "labels" (clj->js labels))
    (obj/set principal-data "data" (clj->js (mapv :principal-repaid splits)))
    (obj/set interest-data "data" (clj->js (mapv :interest-serviced splits)))
    (.update graph)))

(defn graph-inner
  []
  (let [local (r/atom nil) ; normal atom?
        update
        (fn [comp]
          (let [{:keys [splits]} (r/props comp)]
            (set-data! (:graph @local) splits)))
        render
        (fn [comp]
          (let [{:keys [splits]} (r/props comp)
                labels           (take (count splits) (iterate inc 2019))
                ctx              (-> js/document
                                     (.getElementById "graph")
                                     (.getContext "2d"))]
            (if-let [graph (:graph @local)] (.destroy graph))
            (reset! local {:graph (js/Chart. ctx config)})
            (update comp)))
        destroy
        (fn [comp]
          (.destroy (:graph @local)))]
    (r/create-class
     {:reagent-render         (fn [] [:canvas#graph.container])
      :component-did-mount    render
      :component-did-update   update
      :component-will-unmount destroy
      :display-name           "graph"})))

(defn graph-view
  [splits]
  [graph-inner {:splits (rest splits)}])

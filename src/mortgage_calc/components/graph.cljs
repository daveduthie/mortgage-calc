(ns mortgage-calc.components.graph
  (:require
   [cljsjs.chartjs]
   ;; [cljsjs.chartist]
   [mortgage-calc.util :as util]
   [reagent.core :as r]))

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
    :options {:scales {:yAxes [{:stacked true}]}}}))

(defn set-data!
  [graph splits]
  (let [labels         (clj->js (take (count splits) (iterate inc 2019)))
        principals     (clj->js (mapv :principal-repaid splits))
        interests      (clj->js (mapv :interest-serviced splits))]
    (js/setData graph labels principals interests)
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

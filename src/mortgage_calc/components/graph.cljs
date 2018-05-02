(ns mortgage-calc.components.graph
  (:require
   [mortgage-calc.util :as util]
   [reagent.core :as r])
  (:import
   goog.object))

(defn make-config
  [splits]
  (let [principals (mapv :principal-repaid splits)
        interests  (mapv :interest-serviced splits)
        labels     (take (count splits) (iterate inc 2019))]
    (clj->js
     {:type    "line"
      :data    {:labels   labels
                :datasets [{:label           "Principal repaid"
                            :data            principals
                            :backgroundColor "rgba(0, 255, 0, 0.4)"
                            :borderWidth     1}
                           {:label           "Interest serviced"
                            :data            interests
                            :backgroundColor "rgba(255, 0, 0, 0.4)"
                            :borderWidth     1}]}
      :options {:scales {:yAxes [{:stacked true}]}}})))

(defn graph-inner [graph]
  (let [local (r/atom nil)
        update
        (fn [comp]
          (let [{:keys [splits]} (r/props comp)
                splits           (rest splits) ; time zero is always empty
                labels           (take (count splits) (iterate inc 2019))
                ctx              (-> js/document
                                     (.getElementById "graph")
                                     (.getContext "2d"))]
            ;; Would prefer something like:
            #_ (set! (-> @local :graph .-data) (make-config splits))
            #_ (.update (:graph @local))
            (if-let [graph (:graph @local)]
              (.destroy graph))
            (reset! local {:graph (js/Chart. ctx (make-config splits))})))]
    (r/create-class
     {:reagent-render       (fn [] [:canvas#graph.container])
      :component-did-mount  update
      :component-did-update update
      :display-name         "graph"})))

(defn graph-view
  [splits]
  [graph-inner {:splits splits}])

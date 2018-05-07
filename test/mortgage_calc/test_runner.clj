(ns mortgage-calc.test-runner
  (:require
   [cljs.build.api :as b]
   [doo.core :as doo]
   [integrant.core :as ig]))

(defn doo-test
  [{:keys [js-env compiler-opts] :as doo-opts}]
  ;; (prn ::doo)
  ;; (clojure.pprint/pprint doo-opts)
  (doo/run-script js-env compiler-opts doo-opts))

(def default-out "target/test")
(def default-src-paths ["src" "test"])
(def path (System/getProperty "user.dir"))

(def base-compiler-opts
  {:main           'mortgage-calc.test-runner
   :verbose        false ; set to true to see what's being recompiled
   :compiler-stats true
   :cache-analysis true})

(defn merge-opts
  [out]
  (let [out (str path "/" out)]
    (merge base-compiler-opts
           {:output-dir out
            :asset-path out
            :output-to  (str out "/tests.js")})))

(defn test!
  [{:keys [src-paths out js-env doo]}]
  (let [opts (merge-opts out)]
    (b/build (apply b/inputs src-paths) opts)
    (doo-test (assoc doo :compiler-opts opts))))

;; src-paths -> where cljs sources live, default src + test
;; out -> where to put generated test build, default target/test
;; doo -> options to pass through to doo
;; karma-port -> port to run karma server on
(defmethod ig/init-key ::test
  [_ {:keys [src-paths out doo no-op?]
      :or   {src-paths default-src-paths
             out       default-out}}]
  (let [{:keys [karma-port js-env]
         :or   {karma-port 9000 js-env :chrome}} doo
        doo'
        (-> doo
            (assoc-in [:paths :karma]
                      (str "karma --port=" karma-port " --no-colors"))
            (assoc :js-env js-env))]
    (if no-op?
      (prn ::no-op)
      (test!
       {:src-paths src-paths
        :js-env    js-env
        :out       out
        :doo       doo'}))))

(defmethod ig/halt-key! ::test
  [_ _])

(ns mortgage-calc.util
  (:require
   [goog.string :as gstring]
   [goog.string.format :as gformat]))

(enable-console-print!)

;; Miscellaneous ------------------------------------------------------

(defn log-error
  [e]
  (.error js/console (str "error:" e)))

(defn pow [n pow]
  (.pow js/Math n pow))

(defn round-currency
  [amt]
  (.toFixed amt 2))

(comment
  (assert (= (round-currency 2.456)
             (round-currency 2.458))))

(defn format-rands
  [amt]
  (str "R " (gstring/format "%.2f" amt)))

;; Calculation --------------------------------------------------------

(defn normalise-input
  [{:keys [price deposit years interest repayment]}]
  {:term     (* years 12)
   :r        (/ interest 12 100)
   :c        repayment
   :c-annual (* repayment 12)
   :P        (- price deposit)})

(defn principal-at-month [r c P n]
  (let [factor (pow (inc r) n)]
    (- (* factor P)
       (/ (* (dec factor) c) r))))

(defn annual-principals
  [{:keys [r c P term]}]
  (mapv (fn [n] (principal-at-month r c P n))
        (range 0 (+ term 2) 12)))

;; FIXME: I'm ugly
(defn annual-splits
  [doc]
  (let [normalised       (normalise-input doc)
        annual-repayment (:c-annual normalised)
        principals       (annual-principals normalised)]
    (loop [p-last   (first principals)
           [p & ps] principals
           accum    []]
      (if p
        (let [principal-repaid  (round-currency (- p-last p))
              interest-serviced (if-not (= p p-last)
                                  (round-currency
                                   (- annual-repayment principal-repaid))
                                  0)]
          (recur p
                 ps
                 (conj accum {:principal         p
                              :principal-repaid  principal-repaid
                              :interest-serviced interest-serviced})))
        accum))))

(ns mortgage-calc.util
  (:require
   [goog.string :as gstring]
   [goog.string.format :as gformat]))

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

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn numeric-input
  [label id]
  (row label [:input.form-control {:field :numeric
                                   :id    [id]
                                   :fmt   "%.2f"}]))

(defn normalise-input
  [{:keys [:mortgage-calc.components.input/purchase-price
           :mortgage-calc.components.input/deposit
           :mortgage-calc.components.input/years
           :mortgage-calc.components.input/quoted-interest-rate
           :mortgage-calc.components.input/monthly-repayment]}]
  {:term     (* years 12)
   :r        (/ quoted-interest-rate 12 100)
   :c        monthly-repayment
   :c-annual (* monthly-repayment 12)
   :P        (- purchase-price deposit)})

(defn principal-at-month [r c P n]
  (let [factor (pow (inc r) n)]
    (- (* factor P)
       (/ (* (dec factor) c) r))))

(defn annual-principals
  [{:keys [r c P term]}]
  (mapv (fn [n] (principal-at-month r c P n))
        (range 0 (+ term 2) 12)))

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
              interest-serviced (if-not (zero? principal-repaid)
                                  (round-currency
                                   (- annual-repayment principal-repaid))
                                  0)]
          (recur p
                 ps
                 (conj accum {:principal         p
                              :principal-repaid  principal-repaid
                              :interest-serviced interest-serviced})))
        accum))))

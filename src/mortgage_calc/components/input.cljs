(ns mortgage-calc.components.input
  (:require
   [mortgage-calc.components.graph :as graph]
   [mortgage-calc.components.table :as table]
   [mortgage-calc.util :as util]
   [reagent-forms.core :as forms]
   [reagent.core :as r]))

(def form-template
  [:div
   (util/numeric-input "Purchase price" ::purchase-price)
   (util/numeric-input "Deposit paid" ::deposit)

   ;; (util/numeric-input "Bond term in years" ::years)
   (util/row
    [:label
     {:field :label
      :preamble "Term of the bond in years: "
      :placeholder "N/A"
      :id [::years]}]
    [:input.form-control
     {:field :range :min 1 :max 20 :id [::years]}])
   (util/numeric-input "Quoted annual interest rate" ::quoted-interest-rate)])

(def input-form-events
  #{::purchase-price
    ::deposit
    ::years
    ::quoted-interest-rate})

(defn calculate-repayment
  [{::keys [quoted-interest-rate years purchase-price deposit]}]
  (let [term          (* years 12)
        interest-rate (/ quoted-interest-rate 12 100)
        factor        (util/pow (inc interest-rate) term)]
    (if (zero? interest-rate)
      (/ purchase-price term)
      (/ (* interest-rate (- purchase-price deposit) factor)
         (dec factor)))))

(defn input-form [doc event-receiver]
  (fn [doc event-receiver]
    [:div
     [:hr]
     [forms/bind-fields
      form-template
      doc
      (fn [[id] val _]
        (event-receiver id val) @doc)
      (fn [_ _ _]
        (when (every? @doc input-form-events)
          (event-receiver ::monthly-repayment
                          (calculate-repayment @doc)) @doc))]

     (when-let [repayment (::monthly-repayment @doc)]
       (util/row "Monthly repayment" (util/format-rands repayment)))
     [:hr]

     (util/row ""
               [:div
                [:button.btn.btn-success
                 {:on-click #(event-receiver ::view-type :table)}
                 "table view"]
                [:button.btn.btn-success
                 {:on-click #(event-receiver ::view-type :graph)}
                 "graph view"]])]))


(defn calculation-view
  [doc event-receiver]
  ;; Prevent recalculating splits on every event.
  ;; A cursor over several keys would obviate the need for this hack.
  (let [last-repayment (r/atom nil)]
    (fn []
      (let [repayment (::monthly-repayment @doc)]
        (when (not= repayment @last-repayment)
          (reset! last-repayment repayment)
          #_
          (event-receiver ::annual-splits (util/annual-splits @doc))))

      (let [splits   (util/annual-splits @doc)
            #_(r/cursor doc [::annual-splits])
            view-type (r/cursor doc [::view-type])]
        (prn :send)
        (case @view-type
          :table [table/table-view splits]
          :graph [graph/graph-view splits]
          [:div]))))

  #_
  (let [splits (util/annual-splits @doc)
        view-type (::view-type @doc)]
    (case view-type
      :table [table/table-view @splits]
      :graph [graph/graph-view {:splits @splits}]
      [:div])))

(defn input-page
  [doc event-receiver]
  [:div
   [input-form doc event-receiver]
   [calculation-view doc event-receiver]])

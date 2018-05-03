(ns mortgage-calc.components.input
  (:require
   [ajax.core :refer [GET POST]]
   [mortgage-calc.components.graph :as graph]
   [mortgage-calc.components.table :as table]
   [mortgage-calc.events :as ev]
   [mortgage-calc.util :as util]
   [reagent-forms.core :as forms]
   [reagent.core :as r]))

(defn calculate-repayment
  [{:keys [interest years price deposit]}]
  (let [term         (* years 12)
        monthly-rate (/ interest 12 100)
        factor       (util/pow (inc monthly-rate) term)]
    (if (zero? monthly-rate)
      (/ price term)
      (/ (* monthly-rate (- price deposit) factor)
         (dec factor)))))

(def kw-prefix :mortgage-calc.components.input)
(def doc (r/cursor ev/app-state [kw-prefix]))

(ev/register-simple-event-handler ::name)
(ev/register-simple-event-handler ::price)
(ev/register-simple-event-handler ::deposit)
(ev/register-simple-event-handler ::years)
(ev/register-simple-event-handler ::interest)
(ev/register-simple-event-handler ::repayment)
(ev/register-simple-event-handler ::view-type)

;; TODO: validation
(defn save-calculation! [fields handler]
  (POST "/calculations"
        {:params
         (select-keys fields [:name :price :deposit :years :interest :repayment])
         :handler       handler
         :error-handler util/log-error}))

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn input [label type id]
  ;; (prn :input-with id)
  (row label [:input.form-control {:field type :id id}]))

(defn numeric-input
  [label id]
  ;; (prn :input-with id)
  (row label [:input.form-control {:field :numeric
                                   :id    id
                                   :fmt   "%.2f"}]))

(def form-template
  [:div
   (input "Name" :text :name)
   (numeric-input "Purchase price" :price)
   (numeric-input "Deposit paid" :deposit)
   (row
    [:label
     {:field       :label
      :preamble    "Term of the bond in years: "
      :placeholder "0"
      :id          :years}]
    [:input.form-control
     {:field :range :min 1 :max 20 :id :years}])
   (numeric-input "Quoted annual interest rate (%)" :interest)])

(def input-fields [:name :price :deposit :years :interest])

(defn form-complete?
  [doc]
  (every? doc input-fields))

(defn input-form
  []
  [:div
   [:hr]
   [forms/bind-fields
    form-template
    doc
    (fn [[id] val _]
      (ev/emit [kw-prefix id] val))
    (fn [_ _ _]
      (when (form-complete? @doc)
        ;; FIXME: go through event mechanism
        ;; Why does (emit ::repayment ...) work?
        (ev/emit ::plz-fixme nil)
        (swap! doc assoc :repayment (calculate-repayment @doc))))]

   (when-let [repayment (:repayment @doc)]
     (row "Monthly repayment" (util/format-rands repayment)))

   [:hr]
   [:div.row
    [:div.col-md-2]
    [:div.col-sm-2
     [:input.btn.btn-primary
      {:type     :submit
       :on-click (fn [e] (save-calculation! @doc #(js/alert "Saved")))
       :value    "save"}]]
    [:div.col-sm-2
     [:input.btn.btn-secondary
      {:type     :submit
       :on-click (fn [e] (ev/emit ::view-type :table))
       :value    "view table"}]]
    [:div.col-sm-2
     [:input.btn.btn-secondary
      {:type     :submit
       :on-click (fn [e] (ev/emit ::view-type :graph))
       :value    "view graph"}]]]
   [:hr]
   ])

(defn calculation-view
  [doc]
  (if (:repayment @doc)
    (let [splits (util/annual-splits @doc) ]
      (case (:view-type @doc)
        :table [table/table-view splits]
        :graph [graph/graph-view splits]
        [:div]))))

(defn input-page
  []
  [:div
   [input-form]
   [calculation-view doc]])

(ns mortgage-calc.test-runner
  (:require
   [cljs.test :as t :include-macros true]
   [doo.runner :refer-macros [doo-tests doo-all-tests]]
   [mortgage-calc.client]
   [mortgage-calc.util-test]))

(enable-console-print!)
(doo-tests 'mortgage-calc.util-test)

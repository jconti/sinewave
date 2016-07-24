(ns sinewave.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [sinewave.core-test]
   [sinewave.common-test]))

(enable-console-print!)

(doo-tests 'sinewave.core-test
           'sinewave.common-test)

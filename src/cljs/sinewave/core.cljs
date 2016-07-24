(ns sinewave.core)

(enable-console-print!)
(.log js/console "hello clojurescript 2")
(def canvas (.getElementById js/document "myCanvas"))
(def ctx    (.getContext canvas "2d"))
;; Clear canvas before doing anything else
(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))

;; original template code
;(set! (.-innerHTML (js/document.getElementById "app")) "<h1>Hello Chestnut!</h1>")

(ns sinewave.core
  (:refer-clojure :exclude [time])
  (:require cljsjs.rx))

(enable-console-print!)
;(.log js/console "hello clojurescript 2")

(def canvas (.getElementById js/document "myCanvas"))
(def ctx    (.getContext canvas "2d"))

(def interval js/Rx.Observable.interval)
(def time (interval 10))

(defn deg-to-rad [n]
  (* (/ Math/PI 180) n))

(defn sine-coord [x]
  (let [sin (Math/sin (deg-to-rad x))
        y (- 100 (* sin 90))]
      {:x x
       :y y
       :sin sin}))

(def sine-wave
  (.map time sine-coord))

(def colour (.map sine-wave
                  (fn [{:keys [sin]}]
                    (if (< sin 0)
                      "red"
                      "blue"))))

(defn fill-rect [x y colour]
     (set! (.-fillStyle ctx) colour)
     (.fillRect ctx x y 2 2))

(-> (.zip sine-wave colour #(vector % %2))
    (.take 600)
    (.subscribe (fn [[{:keys [x y]} colour]]
                  (fill-rect x y colour))))

;; Clear canvas before doing anything else
(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))

;; original template code
;(set! (.-innerHTML (js/document.getElementById "app")) "<h1>Hello Chestnut!</h1>")

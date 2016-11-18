(ns sinewave.core
  (:refer-clojure :exclude [time concat])
  (:require cljsjs.rx))

(enable-console-print!)
;(.log js/console "hello clojurescript 2")

(def canvas (.getElementById js/document "myCanvas"))
(def ctx    (.getContext canvas "2d"))

(def interval js/Rx.Observable.interval)
(def time (interval 10))

(def red  (.map time (fn [_] "red")))
(def blue (.map time (fn [_] "blue")))

(def concat     js/Rx.Observable.concat)
(def defer      js/Rx.Observable.defer)
(def from-event js/Rx.Observable.fromEvent)

(def mouse-click (from-event canvas "click"))

(def cycle-colour
     (concat (.takeUntil red mouse-click)
             (defer #(concat (.takeUntil blue mouse-click)
                             cycle-colour))))

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

(-> (.zip sine-wave cycle-colour #(vector % %2))
    (.take 600)
    (.subscribe (fn [[{:keys [x y]} colour]]
                  (fill-rect x y colour))))

;; Clear canvas before doing anything else
(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))

;; original template code
;(set! (.-innerHTML (js/document.getElementById "app")) "<h1>Hello Chestnut!</h1>")

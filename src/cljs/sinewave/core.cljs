(ns sinewave.core
  (:refer-clojure :exclude [time])
  (:require [cljsjs.rx]))

(enable-console-print!)

(def canvas (.getElementById js/document "myCanvas"))
(def ctx    (.getContext canvas "2d"))

(def time (js/Rx.Observable.interval 10))

(def red  (.map time (fn [_] "red")))
(def blue (.map time (fn [_] "blue")))

(def mouse-click (js/Rx.Observable.fromEvent canvas "click"))

(def cycle-colour
  (let [switch-to-blue #(.concat (.takeUntil blue mouse-click) cycle-colour)]
     (.concat (.takeUntil red mouse-click)
              (js/Rx.Observable.defer switch-to-blue))))

(defn deg-to-rad [n]
  (* (/ Math/PI 180) n))

(defn sine-coord [x]
   {:x x :y (->> x deg-to-rad Math/sin (* 90) (- 100))})

(def sine-wave
  (.map time sine-coord))

(defn fill-rect [x y colour]
     (set! (.-fillStyle ctx) colour)
     (.fillRect ctx x y 2 2))

;;
;; static rainbow
;;

(def static-rainbow
  (-> ["#FF0000" "#FF7F00" "#FFFF00" "#00FF00" "#0000FF" "#4B0082" "#9400D3"]
      clj->js
      js/Rx.Observable.from
      (.zip time (fn [color _] color))
      .repeat))

;;
;; continuous rainbow (ref. http://krazydad.com/tutorials/makecolors.php)
;;

(defn rgb->color [r g b]
  (let [rgb-str #(str "rgb(" % ")")]
    (->> [r g b] (map Math/round) (interpose ",") (apply str) rgb-str)))

(defn sin->byte-value [x] (-> x (+ 1) (* 127.5) Math/round))

(defn time->sin [freq phase t]
  (-> t (* freq) (+ phase) deg-to-rad Math/sin))

(def continuous-rainbow
  (let [shifted #(partial time->sin 8 %)
        byte-obs-shifted #(-> time (.map (shifted %)) (.map sin->byte-value))
        r (byte-obs-shifted 0)
        g (byte-obs-shifted 120)
        b (byte-obs-shifted 240)]
    (.zip r g b rgb->color)))

;;
;; main
;;

(defn po [obs] (.subscribe obs #(.log js/console %)))

(-> (.zip sine-wave continuous-rainbow #(vector %1 %2))
    (.take 600)
    (.subscribe (fn [[{:keys [x y]} colour]]
                  (fill-rect x y colour))))

;; Clear canvas before doing anything else
(.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
